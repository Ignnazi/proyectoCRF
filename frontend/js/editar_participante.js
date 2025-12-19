// Variables globales
let participanteId = null;
let datosOriginales = {};

// Inicializar al cargar la página
window.addEventListener('DOMContentLoaded', async () => {
    // Obtener ID del participante de la URL
    const params = new URLSearchParams(window.location.search);
    participanteId = params.get('id');

    if (!participanteId) {
        alert('No se especificó un participante');
        window.location.href = 'busqueda.html';
        return;
    }

    // Verificar sesión
    const userId = sessionStorage.getItem('userId');
    const userName = sessionStorage.getItem('userName');

    if (!userId) {
        alert('Debe iniciar sesión');
        window.location.href = 'index.html';
        return;
    }

    // Mostrar nombre de usuario
    document.getElementById('navbarUserName').textContent = userName || 'Usuario';

    // Configurar botones de navegación
    document.getElementById('btnBackToHome').addEventListener('click', (e) => {
        e.preventDefault();
        window.location.href = `busqueda.html?userId=${userId}`;
    });

    document.getElementById('btnLogout').addEventListener('click', (e) => {
        e.preventDefault();
        sessionStorage.clear();
        window.location.href = 'index.html';
    });

    // Cargar datos del participante
    await cargarDatosParticipante();
});

// Cargar todos los datos del participante
async function cargarDatosParticipante() {
    try {
        // Cargar datos principales
        const respPart = await fetch(`http://pacheco.chillan.ubiobio.cl:8034/api/participantecrf/${participanteId}`);
        if (!respPart.ok) throw new Error('Participante no encontrado');
        const participante = await respPart.json();

        // Cargar datos relacionados
        const [antecedente, antropometria, factor, genotipo, habitos, helicobacter, histopatologia, sociodemo] = await Promise.all([
            cargarEntidad('antecedente', participanteId),
            cargarEntidad('antropometria', participanteId),
            cargarEntidad('factor', participanteId),
            cargarEntidad('genotipo', participanteId),
            cargarHabitos(participanteId),
            cargarEntidad('helicobacter', participanteId),
            cargarEntidad('histopatologia', participanteId),
            cargarEntidad('sociodemo', participanteId)
        ]);

        // Guardar datos originales
        datosOriginales = {
            participante,
            antecedente,
            antropometria,
            factor,
            genotipo,
            habitos,
            helicobacter,
            histopatologia,
            sociodemo
        };

        // Renderizar formulario
        renderizarFormulario();

    } catch (error) {
        console.error('Error al cargar datos:', error);
        document.getElementById('editContent').innerHTML = `
            <p style="color: white; text-align: center;">Error al cargar los datos del participante</p>
        `;
    }
}

// Cargar datos de una entidad
async function cargarEntidad(entidad, codPart) {
    try {
        const resp = await fetch(`http://pacheco.chillan.ubiobio.cl:8034/api/${entidad}`);
        if (!resp.ok) return null;

        const lista = await resp.json();
        if (Array.isArray(lista)) {
            // Comparar como string, no como entero
            return lista.find(item => item.codPart === codPart) || null;
        }
        return null;
    } catch (error) {
        console.error(`Error al cargar ${entidad}:`, error);
        return null;
    }
}

// Cargar hábitos (puede haber múltiples)
async function cargarHabitos(codPart) {
    try {
        const resp = await fetch(`http://pacheco.chillan.ubiobio.cl:8034/api/habito`);
        if (!resp.ok) return [];

        const lista = await resp.json();
        if (Array.isArray(lista)) {
            // Comparar como string, no como entero
            return lista.filter(item => item.codPart === codPart);
        }
        return [];
    } catch (error) {
        console.error('Error al cargar hábitos:', error);
        return [];
    }
}

// Renderizar formulario de edición
function renderizarFormulario() {
    const content = document.getElementById('editContent');
    const { participante, antecedente, antropometria, factor, genotipo, habitos, helicobacter, histopatologia, sociodemo } = datosOriginales;

    let html = `
        <!-- Datos del Participante -->
        <div class="section">
            <h2 class="section-title">Datos del Participante - Código: ${participante?.codPart}</h2>
            <div class="info-grid">
                ${crearCampo('Nombre', 'participante.nombre', participante?.nombre, 'text')}
                ${crearCampo('Grupo', 'participante.grupo', participante?.grupo, 'select', false, ['Caso', 'Control'])}
                ${crearCampo('Fecha de Inclusión', 'participante.fechaInclusion', participante?.fechaInclusion?.slice(0, 16), 'datetime-local')}
                ${crearCampo('ID Usuario', 'participante.idUser', participante?.idUser, 'number')}
            </div>
        </div>

        <!-- Datos Sociodemográficos -->
        <div class="section">
            <h2 class="section-title">Datos Sociodemográficos</h2>
            <div class="info-grid">
                ${crearCampo('Edad', 'sociodemo.edad', sociodemo?.edad, 'number')}
                ${crearCampo('Sexo', 'sociodemo.sexo', sociodemo?.sexo, 'select', false, ['Hombre', 'Mujer'])}
                ${crearCampo('Nacionalidad', 'sociodemo.nacionalidad', sociodemo?.nacionalidad, 'text')}
                ${crearCampo('Dirección', 'sociodemo.direccion', sociodemo?.direccion, 'text')}
                ${crearCampo('Zona', 'sociodemo.zona', sociodemo?.zona, 'select', false, ['Urbana', 'Rural'])}
                ${crearCampo('Años de Residencia', 'sociodemo.aniosRes', sociodemo?.aniosRes, 'select', false, ['<5', '5–10', '>10'])}
                ${crearCampo('Educación', 'sociodemo.educacion', sociodemo?.educacion, 'select', false, ['Básico', 'Medio', 'Superior'])}
                ${crearCampo('Ocupación', 'sociodemo.ocupacion', sociodemo?.ocupacion, 'text')}
            </div>
        </div>

        <!-- Antropometría -->
        <div class="section">
            <h2 class="section-title">Antropometría</h2>
            <div class="info-grid">
                ${crearCampo('Peso (kg)', 'antropometria.peso', antropometria?.peso, 'number', false, [], 0.01)}
                ${crearCampo('Estatura (m)', 'antropometria.estatura', antropometria?.estatura, 'number', false, [], 0.01)}
                ${crearCampo('IMC', 'antropometria.imc', antropometria?.imc, 'number', false, [], 0.1)}
            </div>
        </div>

        <!-- Antecedentes -->
        <div class="section">
            <h2 class="section-title">Antecedentes</h2>
            <div class="info-grid">
                ${crearCampo('Diagnóstico', 'antecedente.diagnostico', antecedente?.diagnostico, 'select', false, ['Sí', 'No'])}
                ${crearCampo('Fecha de Diagnóstico', 'antecedente.fechaDiag', antecedente?.fechaDiag?.slice?.(0, 10) || antecedente?.fechaDiag, 'date')}
                ${crearCampo('Familiar con CG', 'antecedente.famCg', antecedente?.famCg, 'select', false, ['Sí', 'No'])}
                ${crearCampo('Familiar con Otro Cáncer', 'antecedente.famOtro', antecedente?.famOtro, 'select', false, ['Sí', 'No'])}
                ${crearCampo('Otro Cáncer', 'antecedente.otroCancer', antecedente?.otroCancer, 'text')}
                ${crearCampo('Otras Enfermedades', 'antecedente.otrasEnfermedades', antecedente?.otrasEnfermedades, 'text')}
                ${crearCampo('Medicamentos', 'antecedente.medicamentos', antecedente?.medicamentos, 'text')}
                ${crearCampo('Cirugía', 'antecedente.cirugia', antecedente?.cirugia, 'select', false, ['Sí', 'No'])}
            </div>
        </div>

        <!-- Factores de Riesgo -->
        <div class="section">
            <h2 class="section-title">Factores de Riesgo</h2>
            <div class="info-grid">
                ${crearCampo('Carnes', 'factor.carnes', factor?.carnes, 'select', false, ['<1/sem', '1–2/sem', '≥3/sem'])}
                ${crearCampo('Salados', 'factor.salados', factor?.salados, 'select', false, ['Sí', 'No'])}
                ${crearCampo('Frutas', 'factor.frutas', factor?.frutas, 'select', false, ['Baja', 'Media', 'Alta'])}
                ${crearCampo('Frituras', 'factor.frituras', factor?.frituras, 'select', false, ['Sí', 'No'])}
                ${crearCampo('Bebidas Calientes', 'factor.bebidasCalientes', factor?.bebidasCalientes, 'select', false, ['Pocas', 'Medias', 'Frecuentes'])}
                ${crearCampo('Pesticidas', 'factor.pesticidas', factor?.pesticidas, 'select', false, ['Sí', 'No'])}
                ${crearCampo('Químicos', 'factor.quimicos', factor?.quimicos, 'select', false, ['Sí', 'No'])}
                ${crearCampo('Detalle Químicos', 'factor.detalleQuimicos', factor?.detalleQuimicos, 'text')}
                ${crearCampo('Humo de Leña', 'factor.humoLena', factor?.humoLena, 'select', false, ['Nunca', 'Estacional', 'Diario'])}
                ${crearCampo('Fuente de Agua', 'factor.fuenteAgua', factor?.fuenteAgua, 'select', false, ['Red', 'Pozo', 'Camión'])}
                ${crearCampo('Tratamiento de Agua', 'factor.tratamientoAgua', factor?.tratamientoAgua, 'select', false, ['Ninguno', 'Hervir', 'Filtro', 'Cloro'])}
            </div>
        </div>

        <!-- Genotipo -->
        <div class="section">
            <h2 class="section-title">Genotipo</h2>
            <div class="info-grid">
                ${crearCampo('Fecha de Toma', 'genotipo.fechaToma', genotipo?.fechaToma?.slice?.(0, 10) || genotipo?.fechaToma, 'date')}
                ${crearCampo('TLR9 rs5743836', 'genotipo.tlr9Rs5743836', genotipo?.tlr9Rs5743836, 'text')}
                ${crearCampo('TLR9 rs187084', 'genotipo.tlr9Rs187084', genotipo?.tlr9Rs187084, 'text')}
                ${crearCampo('miR-146a rs2910164', 'genotipo.mir146aRs2910164', genotipo?.mir146aRs2910164, 'text')}
                ${crearCampo('miR-196a2 rs11614913', 'genotipo.mir196a2Rs11614913', genotipo?.mir196a2Rs11614913, 'text')}
                ${crearCampo('MTHFR rs1801133', 'genotipo.mthfrRs1801133', genotipo?.mthfrRs1801133, 'text')}
                ${crearCampo('DNMT3B rs1569686', 'genotipo.dnmt3bRs1569686', genotipo?.dnmt3bRs1569686, 'text')}
            </div>
        </div>

        <!-- Helicobacter -->
        <div class="section">
            <h2 class="section-title">Helicobacter Pylori</h2>
            <div class="info-grid">
                ${crearCampo('Prueba', 'helicobacter.prueba', helicobacter?.prueba, 'select', false, ['Aliento', 'Antígeno', 'Endoscopía'])}
                ${crearCampo('Resultado', 'helicobacter.resultado', helicobacter?.resultado, 'select', false, ['Positivo', 'Negativo'])}
                ${crearCampo('Antigüedad', 'helicobacter.antiguedad', helicobacter?.antiguedad, 'select', false, ['<1 año', '1–5 años', '>5 años'])}
            </div>
        </div>

        <!-- Histopatología -->
        <div class="section">
            <h2 class="section-title">Histopatología</h2>
            <div class="info-grid">
                ${crearCampo('Tipo', 'histopatologia.tipo', histopatologia?.tipo, 'text')}
                ${crearCampo('Localización', 'histopatologia.localizacion', histopatologia?.localizacion, 'text')}
                ${crearCampo('Estadio', 'histopatologia.estadio', histopatologia?.estadio, 'text')}
            </div>
        </div>

        <!-- Hábitos -->
        <div class="section">
            <h2 class="section-title">Hábitos</h2>
            <div id="habitosContainer">
    `;

    // Agregar hábitos existentes o crear uno vacío
    if (habitos && habitos.length > 0) {
        habitos.forEach((habito, index) => {
            html += crearFormularioHabito(habito, index);
        });
    } else {
        html += crearFormularioHabito(null, 0);
    }

    html += `
            </div>
            <button type="button" class="btn btn-secondary mt-3" onclick="agregarHabito()">+ Agregar Hábito</button>
        </div>
    `;

    content.innerHTML = html;
}

// Crear formulario para un hábito
function crearFormularioHabito(habito, index) {
    return `
        <div class="habito-item" data-index="${index}">
            <h3>Hábito ${index + 1}</h3>
            <div class="info-grid">
                ${crearCampo('Tipo', `habitos.${index}.tipo`, habito?.tipo, 'select', false, ['Fumar', 'Beber', 'Ejercicio', 'Otro'])}
                ${crearCampo('Estado', `habitos.${index}.estado`, habito?.estado, 'select', false, ['Nunca', 'Ex', 'Actual'])}
                ${crearCampo('Frecuencia', `habitos.${index}.frecuencia`, habito?.frecuencia, 'text')}
                ${crearCampo('Cantidad', `habitos.${index}.cantidad`, habito?.cantidad, 'text')}
                ${crearCampo('Años de Consumo', `habitos.${index}.aniosConsumo`, habito?.aniosConsumo, 'text')}
                ${crearCampo('Tiempo Dejado', `habitos.${index}.tiempoDejado`, habito?.tiempoDejado, 'text')}
                ${crearCampo('Edad de Inicio', `habitos.${index}.edadInicio`, habito?.edadInicio, 'number')}
                <input type="hidden" name="habitos.${index}.idHabit" value="${habito?.idHabit || ''}" />
            </div>
            ${index > 0 ? `<button type="button" class="btn btn-danger btn-sm mt-2" onclick="eliminarHabito(${index})">Eliminar Hábito</button>` : ''}
        </div>
    `;
}

// Agregar un nuevo hábito
function agregarHabito() {
    const container = document.getElementById('habitosContainer');
    const habitos = document.querySelectorAll('.habito-item');
    const nuevoIndex = habitos.length;

    const nuevoHabito = document.createElement('div');
    nuevoHabito.innerHTML = crearFormularioHabito(null, nuevoIndex);
    container.appendChild(nuevoHabito.firstElementChild);
}

// Eliminar un hábito
function eliminarHabito(index) {
    const habito = document.querySelector(`.habito-item[data-index="${index}"]`);
    if (habito && confirm('¿Está seguro de eliminar este hábito?')) {
        habito.remove();
    }
}

// Crear campo de formulario
function crearCampo(label, name, value, type = 'text', readonly = false, opciones = [], step = 1) {
    value = value ?? '';

    if (type === 'select') {
        let options = '<option value="">Seleccionar...</option>';
        opciones.forEach(opt => {
            options += `<option value="${opt}" ${value === opt ? 'selected' : ''}>${opt}</option>`;
        });
        return `
            <div class="info-item">
                <label class="info-label">${label}:</label>
                <select name="${name}" class="info-value form-control" ${readonly ? 'disabled' : ''}>
                    ${options}
                </select>
            </div>
        `;
    } else if (type === 'textarea') {
        return `
            <div class="info-item" style="grid-column: 1 / -1;">
                <label class="info-label">${label}:</label>
                <textarea name="${name}" class="info-value form-control" rows="3" ${readonly ? 'readonly' : ''}>${value}</textarea>
            </div>
        `;
    } else {
        return `
            <div class="info-item">
                <label class="info-label">${label}:</label>
                <input type="${type}" name="${name}" value="${value}" class="info-value form-control" ${readonly ? 'readonly' : ''} ${type === 'number' ? `step="${step}"` : ''} />
            </div>
        `;
    }
}

// Guardar cambios
async function guardarCambios() {
    if (!confirm('¿Está seguro de guardar los cambios?')) {
        return;
    }

    const btnSave = document.getElementById('btn-save');
    btnSave.disabled = true;
    btnSave.textContent = '⏳ Guardando...';

    try {
        // Recopilar datos del formulario
        const formData = recopilarDatosFormulario();
        console.log('Datos a guardar:', formData);

        // Helper: Verificar si una entidad tiene datos útiles (más que solo codPart y ID)
        const tieneContenido = (entidad) => {
            if (!entidad) return false;
            const claves = Object.keys(entidad).filter(k =>
                k !== 'codPart' &&
                !k.startsWith('id') &&
                entidad[k] !== null &&
                entidad[k] !== ''
            );
            return claves.length > 0;
        };

        // Guardar cada entidad
        console.log('Guardando participante...');
        await guardarEntidad('participantecrf', formData.participante, participanteId, 'codPart');

        // Sociodemo
        if (formData.sociodemo && (formData.sociodemo.idSocdemo || tieneContenido(formData.sociodemo))) {
            console.log('Guardando sociodemo...', formData.sociodemo);
            await guardarEntidad('sociodemo', formData.sociodemo, formData.sociodemo.idSocdemo, 'idSocdemo');
        }

        // Antropometria
        if (formData.antropometria && (formData.antropometria.idAntrop || tieneContenido(formData.antropometria))) {
            console.log('Guardando antropometria...', formData.antropometria);
            await guardarEntidad('antropometria', formData.antropometria, formData.antropometria.idAntrop, 'idAntrop');
        }

        // Antecedente
        if (formData.antecedente && (formData.antecedente.idAntec || tieneContenido(formData.antecedente))) {
            console.log('Guardando antecedente...', formData.antecedente);
            await guardarEntidad('antecedente', formData.antecedente, formData.antecedente.idAntec, 'idAntec');
        }

        // Factor
        if (formData.factor && (formData.factor.idFac || tieneContenido(formData.factor))) {
            console.log('Guardando factor...', formData.factor);
            await guardarEntidad('factor', formData.factor, formData.factor.idFac, 'idFac');
        }

        // Genotipo
        if (formData.genotipo && (formData.genotipo.idGenotip || tieneContenido(formData.genotipo))) {
            console.log('Guardando genotipo...', formData.genotipo);
            await guardarEntidad('genotipo', formData.genotipo, formData.genotipo.idGenotip, 'idGenotip');
        }

        // Helicobacter
        if (formData.helicobacter && (formData.helicobacter.idHelic || tieneContenido(formData.helicobacter))) {
            console.log('Guardando helicobacter...', formData.helicobacter);
            await guardarEntidad('helicobacter', formData.helicobacter, formData.helicobacter.idHelic, 'idHelic');
        }

        // Histopatologia
        if (formData.histopatologia && (formData.histopatologia.idHisto || tieneContenido(formData.histopatologia))) {
            console.log('Guardando histopatologia...', formData.histopatologia);
            await guardarEntidad('histopatologia', formData.histopatologia, formData.histopatologia.idHisto, 'idHisto');
        }

        // Guardar hábitos
        if (formData.habitos && formData.habitos.length > 0) {
            console.log('Guardando hábitos...', formData.habitos);
            for (const habito of formData.habitos) {
                // Solo guardar hábitos con contenido
                if (tieneContenido(habito) || habito.idHabit) {
                    await guardarEntidad('habito', habito, habito.idHabit, 'idHabit');
                }
            }
        }

        alert('✅ Cambios guardados exitosamente');

        // Recargar datos
        await cargarDatosParticipante();

    } catch (error) {
        console.error('Error al guardar:', error);
        alert('❌ Error al guardar los cambios:\n' + error.message);
    } finally {
        btnSave.disabled = false;
        btnSave.textContent = '💾 Guardar Cambios';
    }
}

// Recopilar datos del formulario
function recopilarDatosFormulario() {
    const inputs = document.querySelectorAll('input, select, textarea');
    const data = {
        participante: { codPart: participanteId },
        sociodemo: datosOriginales.sociodemo ? { ...datosOriginales.sociodemo } : { codPart: participanteId },
        antropometria: datosOriginales.antropometria ? { ...datosOriginales.antropometria } : { codPart: participanteId },
        antecedente: datosOriginales.antecedente ? { ...datosOriginales.antecedente } : { codPart: participanteId },
        factor: datosOriginales.factor ? { ...datosOriginales.factor } : { codPart: participanteId },
        genotipo: datosOriginales.genotipo ? { ...datosOriginales.genotipo } : { codPart: participanteId },
        helicobacter: datosOriginales.helicobacter ? { ...datosOriginales.helicobacter } : { codPart: participanteId },
        histopatologia: datosOriginales.histopatologia ? { ...datosOriginales.histopatologia } : { codPart: participanteId },
        habitos: []
    };

    inputs.forEach(input => {
        const name = input.name;
        if (!name) return;

        const parts = name.split('.');
        let value = input.value;

        // Convertir valores vacíos a null
        if (value === '') {
            value = null;
        }

        if (parts[0] === 'habitos') {
            const index = parseInt(parts[1]);
            const field = parts[2];

            if (!data.habitos[index]) {
                const habitoOriginal = datosOriginales.habitos?.[index];
                data.habitos[index] = habitoOriginal ? { ...habitoOriginal } : { codPart: participanteId };
            }

            if (field === 'idHabit' && value) {
                data.habitos[index].idHabit = parseInt(value);
            } else if (field === 'edadInicio') {
                data.habitos[index][field] = value ? parseInt(value) : null;
            } else {
                data.habitos[index][field] = value;
            }
        } else {
            const entidad = parts[0];
            const field = parts[1];

            if (data[entidad]) {
                if (field === 'codPart') {
                    // codPart debe ser String para participantecrf
                    data[entidad][field] = participanteId;
                } else if (field.startsWith('id') && field !== 'idUser') {
                    data[entidad][field] = value ? parseInt(value) : data[entidad][field];
                } else if (input.type === 'number') {
                    data[entidad][field] = value ? parseFloat(value) : null;
                } else {
                    data[entidad][field] = value;
                }
            }
        }
    });

    // Filtrar hábitos vacíos
    data.habitos = data.habitos.filter(h => h.tipo || h.estado || h.frecuencia || h.cantidad);

    return data;
}

// Guardar una entidad
async function guardarEntidad(endpoint, datos, id, idField) {
    try {
        // Si no hay ID, crear nueva entidad
        if (!id) {
            console.log(`📤 POST /api/${endpoint}`, datos);
            const resp = await fetch(`http://pacheco.chillan.ubiobio.cl:8034/api/${endpoint}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(datos)
            });

            if (!resp.ok) {
                const contentType = resp.headers.get('content-type');
                let errorMsg = `Error ${resp.status}`;
                let fullError = null;

                try {
                    const responseText = await resp.text();
                    console.error(`❌ Respuesta completa del servidor:`, responseText);

                    if (contentType && contentType.includes('application/json')) {
                        try {
                            const errorJson = JSON.parse(responseText);
                            errorMsg = errorJson.message || errorJson.error || JSON.stringify(errorJson);
                            fullError = errorJson;
                        } catch (e) {
                            errorMsg = responseText;
                        }
                    } else {
                        errorMsg = responseText;
                    }
                } catch (e) {
                    errorMsg = `HTTP ${resp.status} - ${resp.statusText}`;
                }

                console.error(`❌ Error al crear ${endpoint}:`, errorMsg);
                console.error(`❌ Datos enviados:`, datos);
                if (fullError) console.error(`❌ Error completo:`, fullError);
                throw new Error(`Error al crear ${endpoint}: ${errorMsg}`);
            }

            // Intentar parsear JSON solo si hay contenido
            const contentType = resp.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                const result = await resp.json();
                console.log(`✅ ${endpoint} creado:`, result);
                return result;
            }
            return null;
        } else {
            // Actualizar entidad existente
            console.log(`📤 PUT /api/${endpoint}/${id}`, datos);
            const resp = await fetch(`http://pacheco.chillan.ubiobio.cl:8034/api/${endpoint}/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(datos)
            });

            if (!resp.ok) {
                const contentType = resp.headers.get('content-type');
                let errorMsg = `Error ${resp.status}`;
                let fullError = null;

                try {
                    const responseText = await resp.text();
                    console.error(`❌ Respuesta completa del servidor:`, responseText);

                    if (contentType && contentType.includes('application/json')) {
                        try {
                            const errorJson = JSON.parse(responseText);
                            errorMsg = errorJson.message || errorJson.error || JSON.stringify(errorJson);
                            fullError = errorJson;
                        } catch (e) {
                            errorMsg = responseText;
                        }
                    } else {
                        errorMsg = responseText;
                    }
                } catch (e) {
                    errorMsg = `HTTP ${resp.status} - ${resp.statusText}`;
                }

                console.error(`❌ Error al actualizar ${endpoint}:`, errorMsg);
                console.error(`❌ Datos enviados:`, datos);
                if (fullError) console.error(`❌ Error completo:`, fullError);
                throw new Error(`Error al actualizar ${endpoint}: ${errorMsg}`);
            }

            // Intentar parsear JSON solo si hay contenido
            const contentType = resp.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                const result = await resp.json();
                console.log(`✅ ${endpoint} actualizado:`, result);
                return result;
            }
            return null;
        }
    } catch (error) {
        console.error(`💥 Error en guardarEntidad (${endpoint}):`, error);
        throw error;
    }
}

