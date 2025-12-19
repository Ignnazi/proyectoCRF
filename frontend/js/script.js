/* Script compartido para index.html y formulario.html
   - Expone funciones en `window` para que los onclick inline sigan funcionando.
   - Verifica existencia de elementos antes de operar (evita errores en páginas parciales).
*/

const API = "http://pacheco.chillan.ubiobio.cl:8034/api/participantecrf";


window.irAFormulario = function irAFormulario(){
  const home = document.getElementById("homeView");
  const form = document.getElementById("formView");
  if (!home || !form) { console.warn('irAFormulario: elementos homeView o formView no presentes en el DOM'); return; }
  home.classList.add("hidden");
  form.classList.remove("hidden");
  if (typeof listarParticipantes === 'function') listarParticipantes();
  window.scrollTo({top:0, behavior:"smooth"});
}

window.volverInicio = function volverInicio(){
  const home = document.getElementById("homeView");
  const form = document.getElementById("formView");
  if (!home || !form) { console.warn('volverInicio: elementos homeView o formView no presentes en el DOM'); return; }
  form.classList.add("hidden");
  home.classList.remove("hidden");
  window.scrollTo({top:0, behavior:"smooth"});
}

window.showMsg = function showMsg(texto, tipo = "ok") {
  const box = document.getElementById("msgBox");
  if (!box) { console.warn('showMsg: msgBox no encontrado'); return; }
  box.className = "msg show " + (tipo === "err" ? "err" : "ok");
  box.textContent = texto;
  setTimeout(() => box.classList.remove("show"), 3500);
}

window.aLocalDateTime = function aLocalDateTime(fechaYYYYMMDD) {
  if (!fechaYYYYMMDD) return "";
  return `${fechaYYYYMMDD}T00:00:00`;
}

window.guardarParticipante = async function guardarParticipante() {
  const nombreEl = document.getElementById("nombre");
  const fechaEl = document.getElementById("fecha");
  if (!nombreEl || !fechaEl) { console.warn('guardarParticipante: elementos nombre o fecha no presentes'); return; }

  const nombre = nombreEl.value.trim();
  const fecha = fechaEl.value;
  const grupoEl = document.querySelector("input[name='grupo']:checked");

  if (!nombre || !fecha || !grupoEl) {
    if (typeof showMsg === 'function') showMsg("Completa nombre, grupo y fecha.", "err");
    return;
  }

  const body = {
    nombre: nombre,
    grupo: grupoEl.value,
    fechaInclusion: aLocalDateTime(fecha)
  };

  try {
    const resp = await fetch(API, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body)
    });

    if (!resp.ok) {
      const txt = await resp.text();
      if (typeof showMsg === 'function') showMsg("Error al guardar: " + txt, "err");
      return;
    }

    if (typeof showMsg === 'function') showMsg("Participante guardado ✅", "ok");

    nombreEl.value = "";
    fechaEl.value = "";
    grupoEl.checked = false;

    if (typeof listarParticipantes === 'function') await listarParticipantes();
  } catch (e) {
    console.error(e);
    if (typeof showMsg === 'function') showMsg("No se pudo conectar al backend (CORS / puerto / API).", "err");
  }
}

window.listarParticipantes = async function listarParticipantes() {
  const tabla = document.getElementById("tablaParticipantes");
  if (!tabla) { console.warn('listarParticipantes: tablaParticipantes no encontrada'); return; }
  tabla.innerHTML = `<tr><td colspan="4">Cargando...</td></tr>`;

  try {
    const resp = await fetch(API);

    if (!resp.ok) {
      const txt = await resp.text();
      tabla.innerHTML = `<tr><td colspan="4">Error: ${txt}</td></tr>`;
      return;
    }

    const lista = await resp.json();

    if (!Array.isArray(lista) || lista.length === 0) {
      tabla.innerHTML = `<tr><td colspan="4">Sin registros</td></tr>`;
      return;
    }

    tabla.innerHTML = "";
    lista.forEach(p => {
      const fechaStr = (p.fechaInclusion || "").toString().slice(0, 10);
      tabla.innerHTML += `
          <tr>
            <td>${p.codPart ?? ""}</td>
            <td>${p.nombre ?? ""}</td>
            <td>${p.grupo ?? ""}</td>
            <td>${fechaStr}</td>
          </tr>
        `;
    });

  } catch (e) {
    console.error(e);
    tabla.innerHTML = `<tr><td colspan="4">No se pudo cargar la lista</td></tr>`;
  }
}

window.irSeccion2 = function irSeccion2() {
  const sec1 = document.getElementById("sec1");
  const sec2 = document.getElementById("sec2");
  if (!sec1 || !sec2) { console.warn('irSeccion2: sec1 o sec2 no encontrados'); return; }
  sec1.classList.add("hidden");
  sec2.classList.remove("hidden");
  window.scrollTo({top:0, behavior:"smooth"});
}

window.volverASeccion1 = function volverASeccion1() {
  const sec1 = document.getElementById("sec1");
  const sec2 = document.getElementById("sec2");
  if (!sec1 || !sec2) { console.warn('volverASeccion1: sec1 o sec2 no encontrados'); return; }
  sec2.classList.add("hidden");
  sec1.classList.remove("hidden");
  window.scrollTo({top:0, behavior:"smooth"});
}

// Registrar event listeners en lugar de usar onclick inline
document.addEventListener('DOMContentLoaded', () => {
  const addIf = (id, fn) => {
    const el = document.getElementById(id);
    if (el) el.addEventListener('click', fn);
  };

  // Mostrar información del usuario en home.html
  const userInfo = document.getElementById('userInfo');
  if (userInfo) {
    const userName = sessionStorage.getItem('userName') || 'Usuario';
    userInfo.textContent = `${userName}`;
  }

  // Mostrar nombre del usuario en el navbar de home.html
  const navbarUserName = document.getElementById('navbarUserName');
  if (navbarUserName) {
    const userName = sessionStorage.getItem('userName') || 'Usuario';
    navbarUserName.textContent = `${userName}`;
  }

  // Actualizar el título de bienvenida en home.html
  const welcomeTitle = document.getElementById('welcomeTitle');
  if (welcomeTitle) {
    const userName = sessionStorage.getItem('userName') || 'Usuario';
    welcomeTitle.innerHTML = `Bienvenido<br>${userName}`;
  }

  // Botón de cerrar sesión en home.html
  const btnLogout = document.getElementById('btnLogout');
  if (btnLogout) {
    btnLogout.addEventListener('click', () => {
      sessionStorage.clear();
      window.location.href = 'index.html';
    });
  }

  addIf('btnLogin', () => alert('Login después :)'));
  // El botón de formulario debe navegar a formulario.html (no existe #formView en index.html)
  addIf('btnForm', () => { window.location.href = 'formulario.html'; });
  addIf('btnInfo', () => { window.location.href = 'busqueda.html'; });

  // Si estamos en la página del formulario, inicializamos ciertas acciones específicas
  if (document.getElementById('formView')) {
    if (typeof listarParticipantes === 'function') listarParticipantes();
    const backForm = document.getElementById('btnBackForm');
    if (backForm) backForm.addEventListener('click', () => { window.location.href = 'home.html'; });
  }


  addIf('btnGuardar', () => { if (typeof guardarParticipante === 'function') guardarParticipante(); });
  addIf('btnNextForm', () => { if (typeof irSeccion2 === 'function') irSeccion2(); });
  addIf('btnBackSec2', () => { if (typeof volverASeccion1 === 'function') volverASeccion1(); });
});

