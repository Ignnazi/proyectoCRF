/* Script para la página de búsqueda de participantes */

const API = "http://localhost:8080/api/participantecrf";
let todosLosParticipantes = [];
let participantesFiltrados = [];
let userIdLogueado = null;

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
        todosLosParticipantes = listaCompleta.filter(p =>
            p.idUser === parseInt(userIdLogueado, 10)
        );

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
                <a href="editar_participante.html?id=${p.codPart}" style="color: #28a745; text-decoration: none; font-weight: bold;">Editar ✏️</a>
            </td>
        `;
        tabla.appendChild(tr);
    });
}

// Aplicar filtros
function aplicarFiltros() {
    const grupoFiltro = document.getElementById("filtroGrupo").value.toLowerCase();
    const nombreFiltro = document.getElementById("filtroNombre").value.toLowerCase().trim();
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
            const [sociodemo, antropometria, antecedente, factor, genotipo, habitos, helicobacter, histopatologia] =
                await Promise.all([
                    obtenerDatosEntidad('sociodemo', p.codPart),
                    obtenerDatosEntidad('antropometria', p.codPart),
                    obtenerDatosEntidad('antecedente', p.codPart),
                    obtenerDatosEntidad('factor', p.codPart),
                    obtenerDatosEntidad('genotipo', p.codPart),
                    obtenerDatosEntidad('habito', p.codPart),
                    obtenerDatosEntidad('helicobacter', p.codPart),
                    obtenerDatosEntidad('histopatologia', p.codPart)
                ]);

            // Construir fila con todos los datos (EXCLUYENDO nombre y dirección)
            const fila = {
                // Identificación del participante
                'Código': p.codPart || '',
                'Grupo': p.grupo || '',
                'Fecha Inclusión': (p.fechaInclusion || '').toString().slice(0, 10),

                // Datos Sociodemográficos (SIN nombre ni dirección)
                'Edad': sociodemo?.edad || '',
                'Sexo': sociodemo?.sexo || '',
                'Nacionalidad': sociodemo?.nacionalidad || '',
                'Zona': sociodemo?.zona || '',
                'Años Residencia': sociodemo?.aniosRes || '',
                'Educación': sociodemo?.educacion || '',
                'Ocupación': sociodemo?.ocupacion || '',

                // Antropometría
                'Peso (kg)': antropometria?.peso || '',
                'Estatura (cm)': antropometria?.estatura || '',
                'IMC': antropometria?.imc || '',

                // Antecedentes
                'Diagnóstico': antecedente?.diagnostico || '',
                'Fecha Diagnóstico': antecedente?.fechaDiag ? antecedente.fechaDiag.toString().slice(0, 10) : '',
                'Familiar CG': antecedente?.famCg || '',
                'Familiar Otro Cáncer': antecedente?.famOtro || '',
                'Otro Cáncer': antecedente?.otroCancer || '',
                'Otras Enfermedades': antecedente?.otrasEnfermedades || '',
                'Medicamentos': antecedente?.medicamentos || '',
                'Cirugía': antecedente?.cirugia || '',

                // Factores de Riesgo
                'Consumo Carnes': factor?.carnes || '',
                'Alimentos Salados': factor?.salados || '',
                'Consumo Frutas': factor?.frutas || '',
                'Frituras': factor?.frituras || '',
                'Bebidas Calientes': factor?.bebidasCalientes || '',
                'Pesticidas': factor?.pesticidas || '',
                'Químicos': factor?.quimicos || '',
                'Detalle Químicos': factor?.detalleQuimicos || '',
                'Humo Leña': factor?.humoLena || '',
                'Fuente Agua': factor?.fuenteAgua || '',
                'Tratamiento Agua': factor?.tratamientoAgua || '',

                // Genotipo
                'Fecha Toma Muestra': genotipo?.fechaToma ? genotipo.fechaToma.toString().slice(0, 10) : '',
                'TLR9 rs5743836': genotipo?.tlr9Rs5743836 || '',
                'TLR9 rs187084': genotipo?.tlr9Rs187084 || '',
                'miR146a rs2910164': genotipo?.mir146aRs2910164 || '',
                'miR196a2 rs11614913': genotipo?.mir196a2Rs11614913 || '',
                'MTHFR rs1801133': genotipo?.mthfrRs1801133 || '',
                'DNMT3B rs1569686': genotipo?.dnmt3bRs1569686 || '',

                // Hábitos (concatenar si hay varios)
                'Hábitos': habitos && habitos.length > 0
                    ? habitos.map((h, i) => `${i+1}. ${h.tipo}: ${h.estado} (${h.frecuencia || 'N/A'})`).join('; ')
                    : '',

                // Helicobacter
                'H. Pylori Prueba': helicobacter?.prueba || '',
                'H. Pylori Resultado': helicobacter?.resultado || '',
                'H. Pylori Antigüedad': helicobacter?.antiguedad || '',

                // Histopatología
                'Histopatología Tipo': histopatologia?.tipo || '',
                'Histopatología Localización': histopatologia?.localizacion || '',
                'Histopatología Estadio': histopatologia?.estadio || ''
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

// Event listeners
document.addEventListener("DOMContentLoaded", () => {
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

    // Aplicar filtros al presionar Enter en los campos de texto
    document.getElementById("filtroNombre").addEventListener("keypress", (e) => {
        if (e.key === "Enter") aplicarFiltros();
    });

    // Aplicar filtros automáticamente al cambiar fechas o grupo
    document.getElementById("filtroGrupo").addEventListener("change", aplicarFiltros);
    document.getElementById("filtroFechaDesde").addEventListener("change", aplicarFiltros);
    document.getElementById("filtroFechaHasta").addEventListener("change", aplicarFiltros);
});

