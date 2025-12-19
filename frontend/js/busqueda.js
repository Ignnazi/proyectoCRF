/* Script para la página de búsqueda de participantes */

const API = "http://localhost:8080/api/participantecrf";
let todosLosParticipantes = [];
let participantesFiltrados = [];
let userIdLogueado = null;

// Mostrar nombre del usuario en el navbar y configurar logout
document.addEventListener('DOMContentLoaded', () => {
    const userName = sessionStorage.getItem('userName');
    const navbarUserName = document.getElementById('navbarUserName');
    if (navbarUserName && userName) {
        navbarUserName.textContent = userName;
    }

    // Configurar botón de cerrar sesión
    const btnLogout = document.getElementById('btnLogout');
    if (btnLogout) {
        btnLogout.addEventListener('click', (e) => {
            e.preventDefault();
            if (confirm('¿Estás seguro de que deseas cerrar sesión?')) {
                sessionStorage.clear();
                window.location.href = 'index.html';
            }
        });
    }
});

// Cargar todos los participantes al iniciar
async function cargarParticipantes() {
    const tabla = document.getElementById("tablaResultados");
    tabla.innerHTML = `<tr><td colspan="5">Cargando...</td></tr>`;

    // Obtener el userId del sessionStorage
    userIdLogueado = sessionStorage.getItem('userId');

    if (!userIdLogueado) {
        tabla.innerHTML = `<tr><td colspan="5">Por favor, inicia sesión para ver los participantes</td></tr>`;
        setTimeout(() => {
            window.location.href = 'index.html';
        }, 2000);
        return;
    }

    try {
        const resp = await fetch(API);

        if (!resp.ok) {
            const txt = await resp.text();
            tabla.innerHTML = `<tr><td colspan="5">Error: ${txt}</td></tr>`;
            return;
        }

        const listaCompleta = await resp.json();

        // Filtrar solo los participantes del usuario logueado
        const participantesUsuario = listaCompleta.filter(p =>
            p.idUser === parseInt(userIdLogueado, 10)
        );

        // Cargar datos de sociodemografía para cada participante
        const respSociodemo = await fetch('http://localhost:8080/api/sociodemo');
        const sociodemoData = respSociodemo.ok ? await respSociodemo.json() : [];

        console.log('Datos de sociodemografía cargados:', sociodemoData.length, 'registros');

        // Combinar datos de participante con sociodemografía
        todosLosParticipantes = participantesUsuario.map(p => {
            const sociodemo = Array.isArray(sociodemoData)
                ? sociodemoData.find(s => s.codPart === p.codPart)
                : null;

            const participanteConDatos = {
                ...p,
                edad: sociodemo?.edad || null,
                sexo: sociodemo?.sexo || null
            };

            // Log para depuración
            if (sociodemo) {
                console.log(`Participante ${p.codPart}: edad=${sociodemo.edad}, sexo=${sociodemo.sexo}`);
            } else {
                console.log(`Participante ${p.codPart}: SIN datos de sociodemografía`);
            }

            return participanteConDatos;
        });

        console.log('Total de participantes cargados:', todosLosParticipantes.length);

        participantesFiltrados = [...todosLosParticipantes];
        mostrarResultados(participantesFiltrados);

    } catch (e) {
        console.error(e);
        tabla.innerHTML = `<tr><td colspan="5">No se pudo conectar al backend</td></tr>`;
    }
}

// Mostrar resultados en la tabla
function mostrarResultados(lista) {
    const tabla = document.getElementById("tablaResultados");
    const infoDiv = document.getElementById("resultsInfo");
    const countSpan = document.getElementById("resultCount");

    if (!Array.isArray(lista) || lista.length === 0) {
        tabla.innerHTML = `<tr><td colspan="5">No se encontraron participantes</td></tr>`;
        infoDiv.style.display = "none";
        return;
    }

    // Actualizar información de resultados
    countSpan.textContent = lista.length;
    infoDiv.style.display = "block";

    // Generar filas de la tabla
    tabla.innerHTML = "";
    lista.forEach(p => {
        const fechaStr = (p.fechaInclusion || "").toString().slice(0, 10);
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td>${p.codPart ?? ""}</td>
            <td>${p.nombre ?? ""}</td>
            <td>${p.grupo ?? ""}</td>
            <td>${fechaStr}</td>
            <td>
                <a href="detalle_participante.html?id=${p.codPart}" style="color: #2d89ff; text-decoration: none; font-weight: bold; margin-right: 10px;">Ver más →</a>
                <a href="editar_participante.html?id=${p.codPart}" style="color: #28a745; text-decoration: none; font-weight: bold; margin-right: 10px;">Editar ✏️</a>
                <button onclick="eliminarParticipante('${p.codPart}', '${p.nombre}')" class="btn btn-danger btn-sm" style="padding: 4px 12px; font-size: 13px;">Eliminar 🗑️</button>
            </td>
        `;
        tabla.appendChild(tr);
    });
}

// Aplicar filtros
function aplicarFiltros() {
    const grupoFiltro = document.getElementById("filtroGrupo").value.toLowerCase();
    const nombreFiltro = document.getElementById("filtroNombre").value.toLowerCase().trim();
    const sexoFiltro = document.getElementById("filtroSexo").value;
    const edadMinFiltro = document.getElementById("filtroEdadMin").value;
    const edadMaxFiltro = document.getElementById("filtroEdadMax").value;
    const fechaDesde = document.getElementById("filtroFechaDesde").value;
    const fechaHasta = document.getElementById("filtroFechaHasta").value;

    participantesFiltrados = todosLosParticipantes.filter(p => {
        // Filtro por grupo
        if (grupoFiltro && p.grupo.toLowerCase() !== grupoFiltro) {
            return false;
        }

        // Filtro por nombre (case-insensitive)
        if (nombreFiltro && !(p.nombre || "").toLowerCase().includes(nombreFiltro)) {
            return false;
        }

        // Filtro por sexo - verificar que el participante tenga dato de sexo
        if (sexoFiltro) {
            if (!p.sexo) {
                // Si hay filtro de sexo pero el participante no tiene dato, excluir
                return false;
            }
            // Comparar ignorando mayúsculas/minúsculas
            if (p.sexo.toLowerCase() !== sexoFiltro.toLowerCase()) {
                return false;
            }
        }

        // Filtro por edad mínima - verificar que el participante tenga dato de edad
        if (edadMinFiltro) {
            const edadMin = parseInt(edadMinFiltro, 10);
            if (p.edad === null || p.edad === undefined || isNaN(p.edad)) {
                // Si hay filtro de edad pero el participante no tiene dato, excluir
                return false;
            }
            if (p.edad < edadMin) {
                return false;
            }
        }

        // Filtro por edad máxima - verificar que el participante tenga dato de edad
        if (edadMaxFiltro) {
            const edadMax = parseInt(edadMaxFiltro, 10);
            if (p.edad === null || p.edad === undefined || isNaN(p.edad)) {
                // Si hay filtro de edad pero el participante no tiene dato, excluir
                return false;
            }
            if (p.edad > edadMax) {
                return false;
            }
        }

        // Filtro por fecha
        const fechaParticipante = (p.fechaInclusion || "").toString().slice(0, 10);

        if (fechaDesde && fechaParticipante < fechaDesde) {
            return false;
        }

        if (fechaHasta && fechaParticipante > fechaHasta) {
            return false;
        }

        return true;
    });

    mostrarResultados(participantesFiltrados);
}

// Limpiar filtros
function limpiarFiltros() {
    document.getElementById("filtroGrupo").value = "";
    document.getElementById("filtroNombre").value = "";
    document.getElementById("filtroSexo").value = "";
    document.getElementById("filtroEdadMin").value = "";
    document.getElementById("filtroEdadMax").value = "";
    document.getElementById("filtroFechaDesde").value = "";
    document.getElementById("filtroFechaHasta").value = "";

    participantesFiltrados = [...todosLosParticipantes];
    mostrarResultados(participantesFiltrados);
}

// Función auxiliar para obtener datos de una entidad
async function obtenerDatosEntidad(entidad, codPart) {
    try {
        const resp = await fetch(`http://localhost:8080/api/${entidad}`);
        if (!resp.ok) return null;

        const lista = await resp.json();
        if (Array.isArray(lista)) {
            const filtrado = lista.filter(item => item.codPart === codPart);
            return entidad === 'habito' ? filtrado : filtrado[0] || null;
        }
        return null;
    } catch (e) {
        console.error(`Error al cargar ${entidad}:`, e);
        return null;
    }
}

// Exportar a Excel con datos completos
async function exportarAExcel() {
    const btnExportar = document.getElementById('btnExportarExcel');

    if (participantesFiltrados.length === 0) {
        alert('No hay participantes para exportar.');
        return;
    }

    try {
        btnExportar.disabled = true;
        btnExportar.textContent = '⏳ Generando Excel...';

        const datosCompletos = [];

        // Cargar datos completos de cada participante
        for (const p of participantesFiltrados) {
            const [sociodemo, antropometria, antecedente, factor, habitos, helicobacter, histopatologia] =
                await Promise.all([
                    obtenerDatosEntidad('sociodemo', p.codPart),
                    obtenerDatosEntidad('antropometria', p.codPart),
                    obtenerDatosEntidad('antecedente', p.codPart),
                    obtenerDatosEntidad('factor', p.codPart),
                    obtenerDatosEntidad('habito', p.codPart),
                    obtenerDatosEntidad('helicobacter', p.codPart),
                    obtenerDatosEntidad('histopatologia', p.codPart)
                ]);

            // Construir fila con todos los datos
            const fila = {
                // ========== IDENTIFICACIÓN DEL PARTICIPANTE ==========
                'Código': p.codPart || '',
                'Nombre': p.nombre || '',
                'Teléfono': p.telefono || '',
                'Correo': p.correo || '',
                'Grupo': p.grupo || '',
                'Fecha Inclusión': (p.fechaInclusion || '').toString().slice(0, 10),

                // ========== DATOS SOCIODEMOGRÁFICOS ==========
                'Edad': sociodemo?.edad || '',
                'Sexo': sociodemo?.sexo || '',
                'Nacionalidad': sociodemo?.nacionalidad || '',
                'Dirección': sociodemo?.direccion || '',
                'Comuna': sociodemo?.comuna || '',
                'Ciudad': sociodemo?.ciudad || '',
                'Zona': sociodemo?.zona || '',
                'Vive más de 5 años': sociodemo?.viveMas5 || '',
                'Educación': sociodemo?.educacion || '',
                'Ocupación': sociodemo?.ocupacion || '',
                'Previsión Salud': sociodemo?.previsionSalud || '',
                'Otra Previsión': sociodemo?.previsionOtra || '',

                // ========== ANTROPOMETRÍA ==========
                'Peso (kg)': antropometria?.peso || '',
                'Estatura (m)': antropometria?.estatura || '',
                'IMC': antropometria?.imc || '',

                // ========== ANTECEDENTES ==========
                'Diagnóstico CG': antecedente?.diagnostico || '',
                'Fecha Diagnóstico': antecedente?.fechaDiag ? antecedente.fechaDiag.toString().slice(0, 10) : '',
                'Familiar con CG': antecedente?.famCg || '',
                'Familiar con Otro Cáncer': antecedente?.famOtro || '',
                'Especificar Otro Cáncer': antecedente?.otroCancer || '',
                'Otras Enfermedades': antecedente?.otrasEnfermedades || '',
                'Medicamentos Gastrointestinales': antecedente?.medGastro || '',
                'Cuáles Medicamentos': antecedente?.medGastroCual || '',
                'Cirugía Gástrica': antecedente?.cirugia || '',

                // ========== FACTORES DE RIESGO ==========
                'Consumo Carnes': factor?.carnes || '',
                'Alimentos Salados': factor?.salados || '',
                'Consumo Frutas': factor?.frutas || '',
                'Frituras': factor?.frituras || '',
                'Alimentos Condimentados': factor?.condimentados || '',
                'Bebidas Calientes': factor?.bebidasCalientes || '',
                'Exposición Pesticidas': factor?.pesticidas || '',
                'Exposición Químicos': factor?.quimicos || '',
                'Detalle Químicos': factor?.detalleQuimicos || '',
                'Humo de Leña': factor?.humoLena || '',
                'Fuente de Agua': factor?.fuenteAgua || '',
                'Otra Fuente Agua': factor?.fuenteAguaOtra || '',
                'Tratamiento Agua': factor?.tratamientoAgua || '',

                // ========== HELICOBACTER PYLORI ==========
                'H. Pylori - Resultado Examen': helicobacter?.resultadoExam || '',
                'H. Pylori - Tipo Examen': helicobacter?.tipoExamen || '',
                'H. Pylori - Otro Examen': helicobacter?.otroExamen || '',
                'H. Pylori - Antigüedad': helicobacter?.antiguedad || '',
                'H. Pylori - Pasado Positivo': helicobacter?.pasadoPositivo || '',
                'H. Pylori - Detalle Pasado': helicobacter?.pasadoDetalle || '',
                'H. Pylori - Tratamiento': helicobacter?.tratamiento || '',
                'H. Pylori - Detalle Tratamiento': helicobacter?.tratamientoDetalle || '',
                'H. Pylori - Uso IBP/Antibióticos': helicobacter?.usoIbpAbx || '',
                'H. Pylori - Examen Repetido': helicobacter?.repetido || '',
                'H. Pylori - Fecha Repetido': helicobacter?.repetidoFecha ? helicobacter.repetidoFecha.toString().slice(0, 10) : '',
                'H. Pylori - Resultado Repetido': helicobacter?.repetidoResultado || '',

                // ========== HÁBITOS ==========
                'Hábitos': habitos && habitos.length > 0
                    ? habitos.map((h, i) =>
                        `${i+1}. Tipo: ${h.tipo || 'N/A'}, Estado: ${h.estado || 'N/A'}, Frecuencia: ${h.frecuencia || 'N/A'}, Cantidad: ${h.cantidad || 'N/A'}, Años: ${h.aniosConsumo || 'N/A'}, Edad Inicio: ${h.edadInicio || 'N/A'}, Tiempo Dejado: ${h.tiempoDejado || 'N/A'}`
                    ).join(' | ')
                    : '',

                // ========== HISTOPATOLOGÍA ==========
                'Histopatología - Tipo': histopatologia?.tipo || '',
                'Histopatología - Localización': histopatologia?.localizacion || '',
                'Histopatología - Estadio': histopatologia?.estadio || ''
            };

            datosCompletos.push(fila);
        }

        // Crear libro de Excel
        const ws = XLSX.utils.json_to_sheet(datosCompletos);
        const wb = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(wb, ws, "Participantes");

        // Ajustar ancho de columnas
        const colWidths = Object.keys(datosCompletos[0] || {}).map(key => ({
            wch: Math.max(key.length, 15)
        }));
        ws['!cols'] = colWidths;

        // Generar archivo y descargar
        const fecha = new Date().toISOString().slice(0, 10);
        XLSX.writeFile(wb, `Participantes_CRF_${fecha}.xlsx`);

        btnExportar.disabled = false;
        btnExportar.textContent = '📊 Exportar a Excel';

    } catch (error) {
        console.error('Error al exportar a Excel:', error);
        alert('Error al generar el archivo Excel. Por favor, intenta nuevamente.');

        btnExportar.disabled = false;
        btnExportar.textContent = '📊 Exportar a Excel';
    }
}

// Función para dicotomizar valores (convertir a 1/0)
function dicotomizar(valor, tipo = 'binario') {
    if (valor === null || valor === undefined || valor === '') {
        return '.'; // Missing value en Stata
    }

    const valorStr = String(valor).toLowerCase().trim();

    // Valores que representan "presencia" o "positivo" = 1
    const valoresPositivos = ['sí', 'si', 'positivo', 'caso', 'actual', 'ex'];
    // Valores que representan "ausencia" o "negativo" = 0
    const valoresNegativos = ['no', 'negativo', 'control', 'nunca', 'desconocido'];

    if (valoresPositivos.includes(valorStr)) {
        return 1;
    } else if (valoresNegativos.includes(valorStr)) {
        return 0;
    }

    // Para valores numéricos
    if (!isNaN(parseFloat(valor))) {
        const num = parseFloat(valor);
        return num > 0 ? 1 : 0;
    }

    // Si no se puede dicotomizar, retornar como está
    return valor;
}

// Exportar a Stata (CSV dicotomizado)
async function exportarAStata() {
    const btnStata = document.getElementById('btnExportarStata');

    if (participantesFiltrados.length === 0) {
        alert('No hay participantes para exportar.');
        return;
    }

    try {
        btnStata.disabled = true;
        btnStata.textContent = '⏳ Generando archivo...';

        const datosDicotomizados = [];

        // Cargar datos completos de cada participante
        for (const p of participantesFiltrados) {
            const [sociodemo, antropometria, antecedente, factor, habitos, helicobacter, histopatologia] =
                await Promise.all([
                    obtenerDatosEntidad('sociodemo', p.codPart),
                    obtenerDatosEntidad('antropometria', p.codPart),
                    obtenerDatosEntidad('antecedente', p.codPart),
                    obtenerDatosEntidad('factor', p.codPart),
                    obtenerDatosEntidad('habito', p.codPart),
                    obtenerDatosEntidad('helicobacter', p.codPart),
                    obtenerDatosEntidad('histopatologia', p.codPart)
                ]);

            // Construir fila con datos dicotomizados
            const fila = {
                // ID y código (mantener como están)
                cod_part: p.codPart || '',

                // GRUPO: Caso=1, Control=0
                grupo: dicotomizar(p.grupo === 'Caso' ? 'Sí' : 'No'),

                // SOCIODEMOGRAFÍA
                edad: sociodemo?.edad || '.',
                sexo: dicotomizar(sociodemo?.sexo === 'Hombre' ? 'Sí' : 'No'), // Hombre=1, Mujer=0
                zona_urbana: dicotomizar(sociodemo?.zona === 'Urbana' ? 'Sí' : 'No'),
                vive_mas5: dicotomizar(sociodemo?.viveMas5),
                educ_superior: dicotomizar(sociodemo?.educacion === 'Superior' ? 'Sí' : 'No'),
                prevision_fonasa: dicotomizar(sociodemo?.previsionSalud === 'Fonasa' ? 'Sí' : 'No'),

                // ANTROPOMETRÍA
                peso: antropometria?.peso || '.',
                estatura: antropometria?.estatura || '.',
                imc: antropometria?.imc || '.',
                imc_sobrepeso: dicotomizar(antropometria?.imc >= 25 ? 'Sí' : 'No'), // IMC >=25 = sobrepeso

                // ANTECEDENTES
                diagnostico_cg: dicotomizar(antecedente?.diagnostico),
                fam_cg: dicotomizar(antecedente?.famCg),
                fam_otro_cancer: dicotomizar(antecedente?.famOtro),
                cirugia_gastrica: dicotomizar(antecedente?.cirugia),
                med_gastro: dicotomizar(antecedente?.medGastro),

                // FACTORES DE RIESGO
                carnes_altas: dicotomizar(factor?.carnes === '≥3/sem' ? 'Sí' : 'No'),
                salados: dicotomizar(factor?.salados),
                frutas_alta: dicotomizar(factor?.frutas === 'Alta' ? 'Sí' : 'No'),
                frituras: dicotomizar(factor?.frituras),
                condimentados: dicotomizar(factor?.condimentados),
                bebidas_calientes_frecuentes: dicotomizar(factor?.bebidasCalientes === 'Frecuentes' ? 'Sí' : 'No'),
                pesticidas: dicotomizar(factor?.pesticidas),
                quimicos: dicotomizar(factor?.quimicos),
                humo_lena_diario: dicotomizar(factor?.humoLena === 'Diario' ? 'Sí' : 'No'),
                agua_no_red: dicotomizar(factor?.fuenteAgua !== 'Red' ? 'Sí' : 'No'),

                // HELICOBACTER PYLORI
                hp_positivo: dicotomizar(helicobacter?.resultadoExam),
                hp_pasado_positivo: dicotomizar(helicobacter?.pasadoPositivo),
                hp_tratamiento: dicotomizar(helicobacter?.tratamiento),
                hp_uso_ibp_abx: dicotomizar(helicobacter?.usoIbpAbx),

                // HÁBITOS (tabaco y alcohol)
                fuma_actual: dicotomizar(habitos?.find(h => h.tipo === 'Tabaco' && h.estado === 'Actual') ? 'Sí' : 'No'),
                fuma_ex: dicotomizar(habitos?.find(h => h.tipo === 'Tabaco' && h.estado === 'Ex') ? 'Sí' : 'No'),
                bebe_actual: dicotomizar(habitos?.find(h => h.tipo === 'Alcohol' && h.estado === 'Actual') ? 'Sí' : 'No'),
                bebe_ex: dicotomizar(habitos?.find(h => h.tipo === 'Alcohol' && h.estado === 'Ex') ? 'Sí' : 'No'),

                // HISTOPATOLOGÍA (solo casos)
                tipo_intestinal: dicotomizar(histopatologia?.tipo === 'Intestinal' ? 'Sí' : 'No'),
                tipo_difuso: dicotomizar(histopatologia?.tipo === 'Difuso' ? 'Sí' : 'No')
            };

            datosDicotomizados.push(fila);
        }

        // Generar CSV compatible con Stata
        const headers = Object.keys(datosDicotomizados[0]);
        let csvContent = headers.join(',') + '\n';

        datosDicotomizados.forEach(fila => {
            const valores = headers.map(header => {
                const valor = fila[header];
                // Escapar valores con comas o comillas
                if (typeof valor === 'string' && (valor.includes(',') || valor.includes('"'))) {
                    return `"${valor.replace(/"/g, '""')}"`;
                }
                return valor;
            });
            csvContent += valores.join(',') + '\n';
        });

        // Crear y descargar archivo
        const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
        const link = document.createElement('a');
        const url = URL.createObjectURL(blob);

        const fecha = new Date().toISOString().slice(0, 10);
        link.setAttribute('href', url);
        link.setAttribute('download', `Participantes_CRF_Dicotomizado_${fecha}.csv`);
        link.style.visibility = 'hidden';

        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);

        btnStata.disabled = false;
        btnStata.textContent = '📊 Exportar a Stata';

        alert(`Archivo exportado correctamente.\n\nPara importar en Stata, usa:\nimport delimited "Participantes_CRF_Dicotomizado_${fecha}.csv", clear`);

    } catch (error) {
        console.error('Error al exportar a Stata:', error);
        alert('Error al generar el archivo. Por favor, intenta nuevamente.');

        btnStata.disabled = false;
        btnStata.textContent = '📊 Exportar a Stata';
    }
}

// Función para eliminar participante y todas sus entidades asociadas
async function eliminarParticipante(codPart, nombre) {
    // Confirmación doble para evitar eliminaciones accidentales
    const confirmacion1 = confirm(`¿Está seguro que desea eliminar al participante "${nombre}" (${codPart})?\n\nEsta acción eliminará también TODOS los datos asociados:\n- Datos sociodemográficos\n- Antecedentes\n- Antropometría\n- Factores de riesgo\n- Hábitos\n- Helicobacter pylori\n- Histopatología\n\nEsta acción NO se puede deshacer.`);

    if (!confirmacion1) {
        return;
    }

    const confirmacion2 = confirm(`ÚLTIMA CONFIRMACIÓN:\n\n¿Realmente desea eliminar permanentemente al participante "${nombre}" y todos sus datos?`);

    if (!confirmacion2) {
        return;
    }

    try {
        // Mostrar mensaje de procesamiento
        const tabla = document.getElementById("tablaResultados");
        const filaOriginal = tabla.innerHTML;
        tabla.innerHTML = `<tr><td colspan="5" style="text-align: center; color: #ffc107;">⏳ Eliminando participante y datos asociados...</td></tr>`;

        // 1. Obtener IDs de todas las entidades asociadas
        const [sociodemo, antropometria, antecedente, factor, habitos, helicobacter, histopatologia] = await Promise.all([
            obtenerDatosEntidad('sociodemo', codPart),
            obtenerDatosEntidad('antropometria', codPart),
            obtenerDatosEntidad('antecedente', codPart),
            obtenerDatosEntidad('factor', codPart),
            obtenerDatosEntidad('habito', codPart),
            obtenerDatosEntidad('helicobacter', codPart),
            obtenerDatosEntidad('histopatologia', codPart)
        ]);

        // 2. Eliminar entidades asociadas (en orden para evitar problemas de FK)
        const promesasEliminacion = [];

        // Eliminar hábitos (puede haber múltiples)
        if (habitos && Array.isArray(habitos)) {
            habitos.forEach(h => {
                if (h.idHabit) {
                    promesasEliminacion.push(
                        fetch(`http://localhost:8080/api/habito/${h.idHabit}`, { method: 'DELETE' })
                    );
                }
            });
        }

        // Eliminar otras entidades
        if (sociodemo?.idSocdemo) {
            promesasEliminacion.push(
                fetch(`http://localhost:8080/api/sociodemo/${sociodemo.idSocdemo}`, { method: 'DELETE' })
            );
        }

        if (antropometria?.idAntrop) {
            promesasEliminacion.push(
                fetch(`http://localhost:8080/api/antropometria/${antropometria.idAntrop}`, { method: 'DELETE' })
            );
        }

        if (antecedente?.idAntec) {
            promesasEliminacion.push(
                fetch(`http://localhost:8080/api/antecedente/${antecedente.idAntec}`, { method: 'DELETE' })
            );
        }

        if (factor?.idFac) {
            promesasEliminacion.push(
                fetch(`http://localhost:8080/api/factor/${factor.idFac}`, { method: 'DELETE' })
            );
        }

        if (helicobacter?.idHelic) {
            promesasEliminacion.push(
                fetch(`http://localhost:8080/api/helicobacter/${helicobacter.idHelic}`, { method: 'DELETE' })
            );
        }

        if (histopatologia?.idHisto) {
            promesasEliminacion.push(
                fetch(`http://localhost:8080/api/histopatologia/${histopatologia.idHisto}`, { method: 'DELETE' })
            );
        }

        // Esperar a que se eliminen todas las entidades asociadas
        await Promise.all(promesasEliminacion);

        // 3. Finalmente, eliminar el participante
        const respParticipante = await fetch(`http://localhost:8080/api/participantecrf/${codPart}`, {
            method: 'DELETE'
        });

        if (!respParticipante.ok) {
            throw new Error('Error al eliminar el participante');
        }

        // 4. Actualizar la lista
        alert(`✅ Participante "${nombre}" eliminado exitosamente junto con todos sus datos asociados.`);

        // Recargar la lista de participantes
        await cargarParticipantes();
        aplicarFiltros();

    } catch (error) {
        console.error('Error al eliminar participante:', error);
        alert(`❌ Error al eliminar el participante: ${error.message}\n\nPor favor, intente nuevamente o contacte al administrador.`);

        // Recargar la lista en caso de error
        await cargarParticipantes();
        aplicarFiltros();
    }
}

// Inicialización
document.addEventListener("DOMContentLoaded", async () => {
    // Mostrar nombre del usuario en el navbar
    const navbarUserName = document.getElementById("navbarUserName");
    if (navbarUserName) {
        const userName = sessionStorage.getItem('userName') || 'Usuario';
        navbarUserName.textContent = `${userName}`;
    }

    // Cargar participantes al inicio
    cargarParticipantes();

    // Botón de búsqueda
    document.getElementById("btnBuscar").addEventListener("click", aplicarFiltros);

    // Botón de limpiar
    document.getElementById("btnLimpiar").addEventListener("click", limpiarFiltros);

    // Cerrar sesión
    const btnLogout = document.getElementById("btnLogout");
    if (btnLogout) {
        btnLogout.addEventListener("click", () => {
            sessionStorage.clear();
            window.location.href = "index.html";
        });
    }

    // Exportar a Excel
    const btnExportarExcel = document.getElementById("btnExportarExcel");
    if (btnExportarExcel) {
        btnExportarExcel.addEventListener("click", () => {
            exportarAExcel();
        });
    }

    // Exportar a Stata
    const btnExportarStata = document.getElementById("btnExportarStata");
    if (btnExportarStata) {
        btnExportarStata.addEventListener("click", () => {
            exportarAStata();
        });
    }

    // Aplicar filtros al presionar Enter en los campos de texto
    document.getElementById("filtroNombre").addEventListener("keypress", (e) => {
        if (e.key === "Enter") aplicarFiltros();
    });

    // Aplicar filtros automáticamente al cambiar fechas, grupo, sexo o edad
    document.getElementById("filtroGrupo").addEventListener("change", aplicarFiltros);
    document.getElementById("filtroSexo").addEventListener("change", aplicarFiltros);
    document.getElementById("filtroEdadMin").addEventListener("input", aplicarFiltros);
    document.getElementById("filtroEdadMax").addEventListener("input", aplicarFiltros);
    document.getElementById("filtroFechaDesde").addEventListener("change", aplicarFiltros);
    document.getElementById("filtroFechaHasta").addEventListener("change", aplicarFiltros);
});

