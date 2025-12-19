/* Script para la página de detalle del participante */

const API_BASE = "http://localhost:8080/api";

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

// Obtener el ID del participante desde la URL
function obtenerIdParticipante() {
    const params = new URLSearchParams(window.location.search);
    return params.get('id');
}

// Cargar toda la información del participante desde diferentes endpoints
async function cargarDetalleParticipante() {
    const contenido = document.getElementById('contenido');
    const codPart = obtenerIdParticipante();

    if (!codPart) {
        contenido.innerHTML = '<div class="error">No se especificó un participante válido.</div>';
        return;
    }

    try {
        // Cargar información del participante
        const respPart = await fetch(`${API_BASE}/participantecrf/${codPart}`);
        if (!respPart.ok) {
            const txt = await respPart.text();
            contenido.innerHTML = `<div class="error">Error al cargar participante: ${txt}</div>`;
            return;
        }
        const participante = await respPart.json();

        // Cargar todas las entidades relacionadas en paralelo
        const [
            antecedentes,
            antropometria,
            sociodemo,
            factores,
            habitos,
            helicobacter,
            histopatologia
        ] = await Promise.all([
            obtenerDatos('antecedente', codPart),
            obtenerDatos('antropometria', codPart),
            obtenerDatos('sociodemo', codPart),
            obtenerDatos('factor', codPart),
            obtenerDatos('habito', codPart),
            obtenerDatos('helicobacter', codPart),
            obtenerDatos('histopatologia', codPart)
        ]);

        // Mostrar toda la información
        mostrarDetalle({
            participante,
            antecedentes,
            antropometria,
            sociodemo,
            factores,
            habitos,
            helicobacter,
            histopatologia
        });

    } catch (e) {
        console.error(e);
        contenido.innerHTML = '<div class="error">No se pudo conectar al backend.</div>';
    }
}

// Función auxiliar para obtener datos de cada entidad
async function obtenerDatos(entidad, codPart) {
    try {
        const resp = await fetch(`${API_BASE}/${entidad}`);
        if (!resp.ok) return null;

        const lista = await resp.json();
        console.log(`Datos de ${entidad}:`, lista);

        // Filtrar por cod_part
        if (Array.isArray(lista)) {
            const filtrado = lista.filter(item => item.codPart === codPart);
            console.log(`Filtrado de ${entidad} para ${codPart}:`, filtrado);
            // Para hábitos puede haber varios, para el resto solo uno
            return entidad === 'habito' ? filtrado : filtrado[0] || null;
        }
        return null;
    } catch (e) {
        console.error(`Error al cargar ${entidad}:`, e);
        return null;
    }
}

// Mostrar toda la información del participante
function mostrarDetalle(data) {
    console.log('Todos los datos del participante:', data);
    const contenido = document.getElementById('contenido');
    const p = data.participante;
    const fechaInclusion = (p.fechaInclusion || "").toString().slice(0, 10);

    let html = `
        <!-- Información básica -->
        <div class="detail-section">
            <h3>Identificación del participante</h3>
            <div class="detail-grid">
                <div class="detail-item">
                    <label>Código:</label>
                    <span>${p.codPart ?? 'N/A'}</span>
                </div>
                <div class="detail-item">
                    <label>Nombre:</label>
                    <span>${p.nombre ?? 'N/A'}</span>
                </div>
                <div class="detail-item">
                    <label>Teléfono:</label>
                    <span>${p.telefono ?? 'N/A'}</span>
                </div>
                <div class="detail-item">
                    <label>Correo Electrónico:</label>
                    <span>${p.correo ?? 'N/A'}</span>
                </div>
                <div class="detail-item">
                    <label>Grupo:</label>
                    <span>${p.grupo ?? 'N/A'}</span>
                </div>
                <div class="detail-item">
                    <label>Fecha de Inclusión:</label>
                    <span>${fechaInclusion || 'N/A'}</span>
                </div>
            </div>
        </div>
    `;

    // Sección: Datos Sociodemográficos
    if (data.sociodemo) {
        const s = data.sociodemo;
        html += `
            <div class="detail-section">
                <h3>Datos Sociodemográficos</h3>
                <div class="detail-grid">
                    <div class="detail-item">
                        <label>Edad:</label>
                        <span>${s.edad ?? 'N/A'}</span>
                    </div>
                    <div class="detail-item">
                        <label>Sexo:</label>
                        <span>${s.sexo ?? 'N/A'}</span>
                    </div>
                    <div class="detail-item">
                        <label>Nacionalidad:</label>
                        <span>${s.nacionalidad ?? 'N/A'}</span>
                    </div>
                    <div class="detail-item">
                        <label>Dirección:</label>
                        <span>${s.direccion ?? 'N/A'}</span>
                    </div>
                    <div class="detail-item">
                        <label>Comuna:</label>
                        <span>${s.comuna ?? 'N/A'}</span>
                    </div>
                    <div class="detail-item">
                        <label>Ciudad:</label>
                        <span>${s.ciudad ?? 'N/A'}</span>
                    </div>
                    <div class="detail-item">
                        <label>Zona:</label>
                        <span>${s.zona ?? 'N/A'}</span>
                    </div>
                    <div class="detail-item">
                        <label>Vive más de 5 años:</label>
                        <span>${s.viveMas5 ?? 'N/A'}</span>
                    </div>
                    <div class="detail-item">
                        <label>Educación:</label>
                        <span>${s.educacion ?? 'N/A'}</span>
                    </div>
                    <div class="detail-item">
                        <label>Ocupación:</label>
                        <span>${s.ocupacion ?? 'N/A'}</span>
                    </div>
                    <div class="detail-item">
                        <label>Previsión de Salud:</label>
                        <span>${s.previsionSalud ?? 'N/A'}</span>
                    </div>
                    <div class="detail-item">
                        <label>Otra Previsión:</label>
                        <span>${s.previsionOtra ?? 'N/A'}</span>
                    </div>
                </div>
            </div>
        `;
    }

    // Sección: Antropometría
    if (data.antropometria) {
        const a = data.antropometria;
        html += `
            <div class="detail-section">
                <h3>Variables Antropométricas</h3>
                <div class="detail-grid">
                    <div class="detail-item">
                        <label>Peso:</label>
                        <span>${a.peso ? a.peso + ' kg' : 'N/A'}</span>
                    </div>
                    <div class="detail-item">
                        <label>Estatura:</label>
                        <span>${a.estatura ? a.estatura + ' cm' : 'N/A'}</span>
                    </div>
                    <div class="detail-item">
                        <label>IMC:</label>
                        <span>${a.imc ?? 'N/A'}</span>
                    </div>
                </div>
            </div>
        `;
    }

    // Sección: Antecedentes
    if (data.antecedentes) {
        const ant = data.antecedentes;
        const fechaDiag = ant.fechaDiag ? ant.fechaDiag.toString().slice(0, 10) : 'N/A';
        html += `
            <div class="detail-section">
                <h3>Antecedentes Clínicos</h3>
                <div class="detail-grid">
                    <div class="detail-item">
                        <label>Diagnóstico de Cáncer Gástrico:</label>
                        <span>${ant.diagnostico ?? 'N/A'}</span>
                    </div>
                    <div class="detail-item">
                        <label>Fecha de Diagnóstico:</label>
                        <span>${fechaDiag}</span>
                    </div>
                    <div class="detail-item">
                        <label>Familiar con Cáncer Gástrico:</label>
                        <span>${ant.famCg ?? 'N/A'}</span>
                    </div>
                    <div class="detail-item">
                        <label>Familiar con Otro Cáncer:</label>
                        <span>${ant.famOtro ?? 'N/A'}</span>
                    </div>
                    <div class="detail-item">
                        <label>Especificar Otro Cáncer:</label>
                        <span>${ant.otroCancer ?? 'N/A'}</span>
                    </div>
                    <div class="detail-item">
                        <label>Otras Enfermedades:</label>
                        <span>${ant.otrasEnfermedades ?? 'N/A'}</span>
                    </div>
                    <div class="detail-item">
                        <label>Medicamentos Gastrointestinales:</label>
                        <span>${ant.medGastro ?? 'N/A'}</span>
                    </div>
                    <div class="detail-item">
                        <label>Cuáles Medicamentos:</label>
                        <span>${ant.medGastroCual ?? 'N/A'}</span>
                    </div>
                    <div class="detail-item">
                        <label>Cirugía Gástrica:</label>
                        <span>${ant.cirugia ?? 'N/A'}</span>
                    </div>
                </div>
            </div>
        `;
    }

    // Sección: Factores de Riesgo
    if (data.factores) {
        const f = data.factores;
        html += `
            <div class="detail-section">
                <h3>Factores dietarios y ambientales</h3>
                <div class="detail-grid">
                    <div class="detail-item">
                        <label>Consumo de Carnes:</label>
                        <span>${f.carnes ?? 'N/A'}</span>
                    </div>
                    <div class="detail-item">
                        <label>Alimentos Salados:</label>
                        <span>${f.salados ?? 'N/A'}</span>
                    </div>
                    <div class="detail-item">
                        <label>Consumo de Frutas:</label>
                        <span>${f.frutas ?? 'N/A'}</span>
                    </div>
                    <div class="detail-item">
                        <label>Frituras:</label>
                        <span>${f.frituras ?? 'N/A'}</span>
                    </div>
                    <div class="detail-item">
                        <label>Alimentos Condimentados:</label>
                        <span>${f.condimentados ?? 'N/A'}</span>
                    </div>
                    <div class="detail-item">
                        <label>Bebidas Calientes:</label>
                        <span>${f.bebidasCalientes ?? 'N/A'}</span>
                    </div>
                    <div class="detail-item">
                        <label>Exposición a Pesticidas:</label>
                        <span>${f.pesticidas ?? 'N/A'}</span>
                    </div>
                    <div class="detail-item">
                        <label>Exposición a Químicos:</label>
                        <span>${f.quimicos ?? 'N/A'}</span>
                    </div>
                    <div class="detail-item">
                        <label>Detalle Químicos:</label>
                        <span>${f.detalleQuimicos ?? 'N/A'}</span>
                    </div>
                    <div class="detail-item">
                        <label>Humo de Leña:</label>
                        <span>${f.humoLena ?? 'N/A'}</span>
                    </div>
                    <div class="detail-item">
                        <label>Fuente de Agua:</label>
                        <span>${f.fuenteAgua ?? 'N/A'}</span>
                    </div>
                    <div class="detail-item">
                        <label>Otra Fuente de Agua:</label>
                        <span>${f.fuenteAguaOtra ?? 'N/A'}</span>
                    </div>
                    <div class="detail-item">
                        <label>Tratamiento de Agua:</label>
                        <span>${f.tratamientoAgua ?? 'N/A'}</span>
                    </div>
                </div>
            </div>
        `;
    }


    // Sección: Hábitos (puede haber varios)
    if (data.habitos && data.habitos.length > 0) {
        html += `
            <div class="detail-section">
                <h3>Hábitos</h3>
        `;
        data.habitos.forEach((h, index) => {
            html += `
                <div style="background: transparent; padding: 12px; border-radius: 6px; margin-bottom: 12px; border: 1px solid #c8cad0;">
                    <h4 style="color: #2d89ff; margin-top: 0;">Hábito ${index + 1}</h4>
                    <div class="detail-grid">
                        <div class="detail-item">
                            <label>Tipo:</label>
                            <span>${h.tipo ?? 'N/A'}</span>
                        </div>
                        <div class="detail-item">
                            <label>Estado:</label>
                            <span>${h.estado ?? 'N/A'}</span>
                        </div>
                        <div class="detail-item">
                            <label>Frecuencia:</label>
                            <span>${h.frecuencia ?? 'N/A'}</span>
                        </div>
                        <div class="detail-item">
                            <label>Cantidad:</label>
                            <span>${h.cantidad ?? 'N/A'}</span>
                        </div>
                        <div class="detail-item">
                            <label>Años de Consumo:</label>
                            <span>${h.aniosConsumo ?? 'N/A'}</span>
                        </div>
                        <div class="detail-item">
                            <label>Tiempo Dejado:</label>
                            <span>${h.tiempoDejado ?? 'N/A'}</span>
                        </div>
                        <div class="detail-item">
                            <label>Edad de Inicio:</label>
                            <span>${h.edadInicio ?? 'N/A'}</span>
                        </div>
                    </div>
                </div>
            `;
        });
        html += `</div>`;
    }

    // Sección: Helicobacter
    if (data.helicobacter) {
        const hel = data.helicobacter;
        const repetidoFecha = hel.repetidoFecha ? hel.repetidoFecha.toString().slice(0, 10) : 'N/A';
        html += `
            <div class="detail-section">
                <h3>Helicobacter Pylori</h3>
                <div class="detail-grid">
                    <div class="detail-item">
                        <label>Resultado del Examen:</label>
                        <span>${hel.resultadoExam ?? 'N/A'}</span>
                    </div>
                    <div class="detail-item">
                        <label>Tipo de Examen:</label>
                        <span>${hel.tipoExamen ?? 'N/A'}</span>
                    </div>
                    <div class="detail-item">
                        <label>Otro Examen:</label>
                        <span>${hel.otroExamen ?? 'N/A'}</span>
                    </div>
                    <div class="detail-item">
                        <label>Antigüedad:</label>
                        <span>${hel.antiguedad ?? 'N/A'}</span>
                    </div>
                    <div class="detail-item">
                        <label>Positivo en el Pasado:</label>
                        <span>${hel.pasadoPositivo ?? 'N/A'}</span>
                    </div>
                    <div class="detail-item">
                        <label>Detalle del Pasado:</label>
                        <span>${hel.pasadoDetalle ?? 'N/A'}</span>
                    </div>
                    <div class="detail-item">
                        <label>Recibió Tratamiento:</label>
                        <span>${hel.tratamiento ?? 'N/A'}</span>
                    </div>
                    <div class="detail-item">
                        <label>Detalle del Tratamiento:</label>
                        <span>${hel.tratamientoDetalle ?? 'N/A'}</span>
                    </div>
                    <div class="detail-item">
                        <label>Uso de IBP/Antibióticos:</label>
                        <span>${hel.usoIbpAbx ?? 'N/A'}</span>
                    </div>
                    <div class="detail-item">
                        <label>Examen Repetido:</label>
                        <span>${hel.repetido ?? 'N/A'}</span>
                    </div>
                    <div class="detail-item">
                        <label>Fecha Examen Repetido:</label>
                        <span>${repetidoFecha}</span>
                    </div>
                    <div class="detail-item">
                        <label>Resultado Examen Repetido:</label>
                        <span>${hel.repetidoResultado ?? 'N/A'}</span>
                    </div>
                </div>
            </div>
        `;
    }

    // Sección: Histopatología
    if (data.histopatologia) {
        const hist = data.histopatologia;
        html += `
            <div class="detail-section">
                <h3>Histopatología</h3>
                <div class="detail-grid">
                    <div class="detail-item">
                        <label>Tipo:</label>
                        <span>${hist.tipo ?? 'N/A'}</span>
                    </div>
                    <div class="detail-item">
                        <label>Localización:</label>
                        <span>${hist.localizacion ?? 'N/A'}</span>
                    </div>
                    <div class="detail-item">
                        <label>Estadio:</label>
                        <span>${hist.estadio ?? 'N/A'}</span>
                    </div>
                </div>
            </div>
        `;
    }

    // Si no hay datos relacionados
    if (!data.sociodemo && !data.antropometria && !data.antecedentes &&
        !data.factores && (!data.habitos || data.habitos.length === 0) &&
        !data.helicobacter && !data.histopatologia) {
        html += `
            <div class="detail-section">
                <p style="color: #6c757d; text-align: center;">
                    No hay información adicional registrada para este participante.
                </p>
            </div>
        `;
    }

    contenido.innerHTML = html;
}

// Función para exportar a PDF
async function exportarPDF() {
    const btnExportar = document.getElementById('btnExportarPDF');
    const btnVolver = document.getElementById('btnVolver');
    const codPart = obtenerIdParticipante();

    if (!codPart) {
        alert('No se puede exportar sin un participante válido.');
        return;
    }

    try {
        // Deshabilitar el botón mientras se genera el PDF
        btnExportar.disabled = true;
        btnExportar.textContent = '⏳ Generando PDF...';

        // Ocultar botones antes de capturar
        btnExportar.style.display = 'none';
        btnVolver.style.display = 'none';

        // Obtener el contenedor principal
        const contenedor = document.querySelector('.crf-container');

        // Agregar clase temporal para mejorar renderizado del PDF
        contenedor.classList.add('pdf-rendering');

        // Pequeño delay para asegurar que el DOM se actualice
        await new Promise(resolve => setTimeout(resolve, 100));

        // Crear un canvas del contenido usando html2canvas con configuración mejorada
        const canvas = await html2canvas(contenedor, {
            scale: 3, // Mayor calidad (aumentado de 2 a 3)
            useCORS: true,
            allowTaint: true,
            logging: false,
            backgroundColor: '#ffffff',
            windowWidth: contenedor.scrollWidth,
            windowHeight: contenedor.scrollHeight,
            // Mejorar renderizado de elementos
            imageTimeout: 0,
            removeContainer: true,
            // Capturar mejor los bordes y sombras
            foreignObjectRendering: false,
            async: true
        });

        // Remover clase temporal y mostrar botones nuevamente
        contenedor.classList.remove('pdf-rendering');
        btnExportar.style.display = 'block';
        btnVolver.style.display = 'block';

        // Crear el PDF usando jsPDF
        const { jsPDF } = window.jspdf;
        const pdf = new jsPDF({
            orientation: 'portrait',
            unit: 'mm',
            format: 'a4',
            compress: true
        });

        // Dimensiones de la página A4
        const pdfWidth = pdf.internal.pageSize.getWidth();
        const pdfHeight = pdf.internal.pageSize.getHeight();

        // Calcular dimensiones de la imagen manteniendo la proporción
        const imgWidth = pdfWidth;
        const imgHeight = (canvas.height * pdfWidth) / canvas.width;

        let heightLeft = imgHeight;
        let position = 0;

        // Convertir canvas a imagen con alta calidad
        const imgData = canvas.toDataURL('image/jpeg', 0.95);

        // Agregar la primera página
        pdf.addImage(imgData, 'JPEG', 0, position, imgWidth, imgHeight, undefined, 'FAST');
        heightLeft -= pdfHeight;

        // Agregar páginas adicionales si es necesario
        while (heightLeft > 0) {
            position = heightLeft - imgHeight;
            pdf.addPage();
            pdf.addImage(imgData, 'JPEG', 0, position, imgWidth, imgHeight, undefined, 'FAST');
            heightLeft -= pdfHeight;
        }

        // Guardar el PDF
        pdf.save(`Participante_${codPart}_${new Date().toISOString().slice(0, 10)}.pdf`);

        // Restaurar el botón
        btnExportar.disabled = false;
        btnExportar.textContent = '📄 Exportar a PDF';

    } catch (error) {
        console.error('Error al exportar PDF:', error);
        alert('Error al generar el PDF. Por favor, intenta nuevamente.');

        // Remover clase temporal si existe
        const contenedor = document.querySelector('.crf-container');
        if (contenedor) {
            contenedor.classList.remove('pdf-rendering');
        }

        // Asegurar que los botones sean visibles nuevamente
        btnExportar.style.display = 'block';
        btnVolver.style.display = 'block';

        // Restaurar el botón
        btnExportar.disabled = false;
        btnExportar.textContent = '📄 Exportar a PDF';
    }
}

// Event listeners
document.addEventListener('DOMContentLoaded', () => {
    cargarDetalleParticipante();

    document.getElementById('btnVolver').addEventListener('click', () => {
        window.location.href = 'busqueda.html';
    });

    document.getElementById('btnExportarPDF').addEventListener('click', () => {
        exportarPDF();
    });
});

