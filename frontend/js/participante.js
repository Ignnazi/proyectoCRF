const API_PART = "http://localhost:8080/api/participantecrf";

window.estadoFormulario = window.estadoFormulario || { codActual: null, grupoActual: null };

// Mostrar nombre del usuario en el navbar
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

function msgPart(texto, tipo = "ok") {
  const box = document.getElementById("msgBox");
  if (!box) return;
  box.className = "msg show " + (tipo === "err" ? "err" : "ok");
  box.textContent = texto;
  setTimeout(() => box.classList.remove("show"), 3500);
}

function getGrupoUI() {
  return document.querySelector("input[name='grupo']:checked")?.value || "";
}

function setAutoCampos(data) {
  const codEl = document.getElementById("codPart");
  const fechaEl = document.getElementById("fechaAuto");
  if (codEl) codEl.value = data?.codPart ?? "";
  const fechaStr = (data?.fechaInclusion || "").toString().slice(0, 10);
  if (fechaEl) fechaEl.value = fechaStr;

  const telEl = document.getElementById("telefono");
  const mailEl = document.getElementById("correo");
  if (telEl) telEl.value = data?.telefono ?? telEl.value ?? "";
  if (mailEl) mailEl.value = data?.correo ?? mailEl.value ?? "";
}

// CAMBIO 1 AQUII: actualizar todos los codPart de las secciones
function setCodSecciones(cod) {
  // Sección 2
  const socio = document.getElementById("codPartSocio");
  if (socio) socio.value = cod || "";

  // Sección 3
  const ant = document.getElementById("codPartAnte");
  if (ant) ant.value = cod || "";

  // Sección 4
  const antrop = document.getElementById("codPartAntrop");
  if (antrop) antrop.value = cod || "";

  // Sección 5
  const hab = document.getElementById("codPartHab");
  if (hab) hab.value = cod || "";

  // Sección 7 (Factores)
  const fac = document.getElementById("codPartFactor");
  if (fac) fac.value = cod || "";

  // Sección 8 (Helicobacter)
  const helic = document.getElementById("codPartHelic");
  if (helic) helic.value = cod || "";

  // Sección 9 (Histopatología)
  const histo = document.getElementById("codPartHisto");
  if (histo) histo.value = cod || "";
}

function limpiarAuto() {
  const codEl = document.getElementById("codPart");
  const fechaEl = document.getElementById("fechaAuto");
  if (codEl) codEl.value = "";
  if (fechaEl) fechaEl.value = "";
  setCodSecciones("");
  window.estadoFormulario.codActual = null;
}

// Función para limpiar TODO el formulario (todas las secciones)
function limpiarTodoElFormulario() {
  // Limpiar Sección 1: Identificación del participante
  const nombre = document.getElementById("nombre");
  const telefono = document.getElementById("telefono");
  const correo = document.getElementById("correo");
  if (nombre) nombre.value = "";
  if (telefono) telefono.value = "";
  if (correo) correo.value = "";

  // Desmarcar grupo
  document.querySelectorAll("input[name='grupo']").forEach(r => r.checked = false);

  // Limpiar código y fecha
  limpiarAuto();

  // Llamar funciones de limpieza de cada sección si existen
  if (typeof window.limpiarSociodemoForm === "function") {
    window.limpiarSociodemoForm();
  }

  if (typeof window.limpiarAntecedenteForm === "function") {
    window.limpiarAntecedenteForm();
  }

  if (typeof window.limpiarAntropometriaForm === "function") {
    window.limpiarAntropometriaForm();
  }

  if (typeof window.limpiarHabitoForm === "function") {
    window.limpiarHabitoForm();
  }

  if (typeof window.limpiarFactorForm === "function") {
    window.limpiarFactorForm();
  }

  if (typeof window.limpiarHelicobacterForm === "function") {
    window.limpiarHelicobacterForm();
  }

  if (typeof window.limpiarHistopatologiaForm === "function") {
    window.limpiarHistopatologiaForm();
  }

  // Scroll al inicio del formulario
  window.scrollTo({ top: 0, behavior: 'smooth' });

  // Mostrar mensaje
  msgPart("Formulario limpiado. Listo para nuevo participante ✅", "ok");
}

function guardarSiListo() {
  const nombre = (document.getElementById("nombre")?.value || "").trim();
  const grupo = getGrupoUI();
  if (!nombre || !grupo) return;
  guardarParticipanteAuto();
}

let guardando = false;

async function listarParticipantes() {
  const tabla = document.getElementById("tablaParticipantes");
  if (!tabla) return;

  tabla.innerHTML = `<tr><td colspan="6">Cargando...</td></tr>`;

  try {
    const resp = await fetch(API_PART);
    if (!resp.ok) {
      const txt = await resp.text();
      tabla.innerHTML = `<tr><td colspan="6">Error: ${txt || resp.status}</td></tr>`;
      return;
    }

    const lista = await resp.json();
    if (!Array.isArray(lista) || lista.length === 0) {
      tabla.innerHTML = `<tr><td colspan="6">Sin registros</td></tr>`;
      return;
    }

    tabla.innerHTML = "";
    lista.forEach(p => {
      const fechaStr = (p.fechaInclusion || "").toString().slice(0, 10);
      tabla.innerHTML += `
        <tr>
          <td>${p.codPart ?? ""}</td>
          <td>${p.nombre ?? ""}</td>
          <td>${p.telefono ?? ""}</td>
          <td>${p.correo ?? ""}</td>
          <td>${p.grupo ?? ""}</td>
          <td>${fechaStr}</td>
        </tr>
      `;
    });
  } catch (e) {
    console.error(e);
    tabla.innerHTML = `<tr><td colspan="6">No se pudo cargar la lista</td></tr>`;
  }
}

async function guardarParticipanteAuto() {
  const nombreEl = document.getElementById("nombre");
  if (!nombreEl) return;

  const nombre = (nombreEl.value || "").trim();
  const telefono = (document.getElementById("telefono")?.value || "").trim();
  const correo = (document.getElementById("correo")?.value || "").trim();
  const grupo = getGrupoUI();

  window.estadoFormulario.grupoActual = grupo || null;

  // Mantener reglas "solo casos" en antecedentes
  if (typeof window.actualizarVisibilidadAntecedente === "function") {
    window.actualizarVisibilidadAntecedente();
  }

  if (!nombre || !grupo) {
    msgPart("Primero escribe el nombre y luego selecciona Caso/Control.", "err");
    limpiarAuto();
    return;
  }

  if (guardando) return;
  guardando = true;

  try {
    // Obtener el userId del usuario logueado
    const userId = sessionStorage.getItem('userId');
    if (!userId) {
      msgPart("Error: No se encontró usuario logueado. Por favor, inicia sesión nuevamente.", "err");
      guardando = false;
      return;
    }

    let url = API_PART;
    let method = "POST";

    if (window.estadoFormulario.codActual) {
      url = `${API_PART}/${encodeURIComponent(window.estadoFormulario.codActual)}`;
      method = "PUT";
    }

    // Preparar el payload con idUser
    const payload = {
      nombre,
      telefono: telefono || null,
      correo: correo || null,
      grupo,
      idUser: parseInt(userId, 10) // Asignar el ID del usuario logueado
    };

    const resp = await fetch(url, {
      method,
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });

    if (!resp.ok) {
      const txt = await resp.text();
      msgPart(txt || "Error guardando participante", "err");
      return;
    }

    const data = await resp.json();
    window.estadoFormulario.codActual = data.codPart || window.estadoFormulario.codActual;

    setAutoCampos(data);
    setCodSecciones(window.estadoFormulario.codActual);

    msgPart("Participante guardado ✅", "ok");
    await listarParticipantes();

    // CAMBIO 2 AQUIII
    if (typeof window.listarSociodemo === "function") await window.listarSociodemo();
    if (typeof window.listarAntecedente === "function") await window.listarAntecedente();
    if (typeof window.listarAntropometria === "function") await window.listarAntropometria();
    if (typeof window.listarHabito === "function") await window.listarHabito();
    if (typeof window.listarFactor === "function") await window.listarFactor();
    if (typeof window.listarHelicobacter === "function") await window.listarHelicobacter();
    if (typeof window.listarHistopatologia === "function") await window.listarHistopatologia();
    if (typeof window.actualizarVisibilidadHistopatologia === "function") window.actualizarVisibilidadHistopatologia();

  } catch (e) {
    console.error(e);
    msgPart("No se pudo conectar al backend.", "err");
  } finally {
    guardando = false;
  }
}

document.addEventListener("DOMContentLoaded", async () => {
  const backForm = document.getElementById("btnBackForm");
  if (backForm) backForm.addEventListener("click", () => { window.location.href = "home.html"; });

  // Botón flotante para nuevo participante
  const btnNuevo = document.getElementById("btnNuevoParticipante");
  if (btnNuevo) {
    btnNuevo.addEventListener("click", () => {
      if (confirm("¿Desea limpiar todos los campos para registrar un nuevo participante?")) {
        limpiarTodoElFormulario();
      }
    });
  }

  document.querySelectorAll("input[name='grupo']").forEach(r => {
    r.addEventListener("change", guardarParticipanteAuto);
  });

  ["nombre", "telefono", "correo"].forEach(id => {
    const el = document.getElementById(id);
    if (el) el.addEventListener("blur", guardarSiListo);
  });

  window.estadoFormulario.grupoActual = getGrupoUI() || null;
  if (typeof window.actualizarVisibilidadAntecedente === "function") {
    window.actualizarVisibilidadAntecedente();
  }

  await listarParticipantes();
});
