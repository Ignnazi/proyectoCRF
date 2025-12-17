/* Script completo para index.html y formulario.html */

const API = "http://localhost:8080/api/participantecrf";
const USER_API = "http://localhost:8080/api/usuario";
const SOCIODEMO_API = "http://localhost:8080/api/sociodemo";

/* ================= NAVEGACIÓN ENTRE VISTAS (SPA simple) ================= */

window.irAFormulario = function irAFormulario(){
  const home = document.getElementById("homeView");
  const form = document.getElementById("formView");
  if (!home || !form) return;
  home.classList.add("hidden");
  form.classList.remove("hidden");
  if (typeof listarParticipantes === 'function') listarParticipantes();
  window.scrollTo({top:0, behavior:"smooth"});
}

window.volverInicio = function volverInicio(){
  const home = document.getElementById("homeView");
  const form = document.getElementById("formView");
  if (!home || !form) return;
  form.classList.add("hidden");
  home.classList.remove("hidden");
  window.scrollTo({top:0, behavior:"smooth"});
}

/* ================= UTILIDADES ================= */

window.showMsg = function showMsg(texto, tipo = "ok", elementId = "msgBox") {
  const box = document.getElementById(elementId);
  if (!box) return;
  box.className = "msg show " + (tipo === "err" ? "err" : "ok");
  box.textContent = texto;
  setTimeout(() => box.classList.remove("show"), 3500);
}

window.aLocalDateTime = function aLocalDateTime(fechaYYYYMMDD) {
  if (!fechaYYYYMMDD) return "";
  return `${fechaYYYYMMDD}T00:00:00`;
}

/* ================= LÓGICA DE PARTICIPANTE (SECCIÓN 1) ================= */

window.guardarParticipante = async function guardarParticipante() {
  const nombreEl = document.getElementById("nombre");
  const fechaEl = document.getElementById("fecha");
  const grupoEl = document.querySelector("input[name='grupo']:checked");

  if (!nombreEl || !fechaEl || !grupoEl) {
    showMsg("Completa nombre, grupo y fecha.", "err");
    return;
  }

  const nombre = nombreEl.value.trim();
  const fecha = fechaEl.value;

  if (!nombre || !fecha) {
    showMsg("Faltan datos obligatorios.", "err");
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
      showMsg("Error al guardar: " + txt, "err");
      return;
    }

    // Respuesta exitosa: Backend devuelve el objeto creado (incluye codPart)
    const participanteCreado = await resp.json();
    
    showMsg(`Participante guardado: ${participanteCreado.codPart}`, "ok");

    // Guardar codPart en el input oculto de la Sección 2 para usarlo después
    const hiddenInput = document.getElementById("currentCodPart");
    if(hiddenInput) {
        hiddenInput.value = participanteCreado.codPart;
    }

    // Limpiar formulario Sección 1
    nombreEl.value = "";
    fechaEl.value = "";
    grupoEl.checked = false;

    // Actualizar tabla
    await listarParticipantes();

  } catch (e) {
    console.error(e);
    showMsg("Error de conexión con el servidor.", "err");
  }
}

window.listarParticipantes = async function listarParticipantes() {
  const tabla = document.getElementById("tablaParticipantes");
  if (!tabla) return;
  tabla.innerHTML = `<tr><td colspan="4">Cargando...</td></tr>`;

  try {
    const resp = await fetch(API);
    if (!resp.ok) {
      tabla.innerHTML = `<tr><td colspan="4">Error al cargar lista</td></tr>`;
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

/* ================= LÓGICA DE SOCIODEMOGRÁFICOS (SECCIÓN 2) ================= */

window.guardarSociodemo = async function guardarSociodemo() {
    // Recuperar el código del participante guardado previamente
    const codPart = document.getElementById("currentCodPart")?.value;
    
    if (!codPart) {
        showMsg("Debes guardar un participante en la Sección 1 primero.", "err", "msgBoxSec2");
        return;
    }

    const edad = document.getElementById("edad").value;
    const sexo = document.getElementById("sexo").value;
    const nacionalidad = document.getElementById("nacionalidad").value;
    const zona = document.getElementById("zona").value;
    const aniosRes = document.getElementById("aniosRes").value;
    const educacion = document.getElementById("educacion").value;
    const ocupacion = document.getElementById("ocupacion").value;

    // Validación básica
    if (!edad || parseInt(edad) < 18) {
        showMsg("La edad es obligatoria y debe ser >= 18.", "err", "msgBoxSec2");
        return;
    }

    const body = {
        codPart: codPart,
        edad: parseInt(edad),
        sexo: sexo,
        nacionalidad: nacionalidad,
        zona: zona,
        aniosRes: aniosRes,
        direccion: "Dirección Genérica", // Valor por defecto si no está en el form
        educacion: educacion,
        ocupacion: ocupacion
    };

    try {
        const resp = await fetch(SOCIODEMO_API, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(body)
        });

        if (resp.ok) {
            showMsg("Datos sociodemográficos guardados correctamente ✅", "ok", "msgBoxSec2");
            // Aquí podrías limpiar campos o avanzar a otra sección
        } else {
            const txt = await resp.text();
            showMsg("Error al guardar: " + txt, "err", "msgBoxSec2");
        }
    } catch (e) {
        console.error(e);
        showMsg("Error de conexión al guardar sociodemográficos.", "err", "msgBoxSec2");
    }
}

/* ================= NAVEGACIÓN INTERNA DEL FORMULARIO ================= */

window.irSeccion2 = function irSeccion2() {
  const sec1 = document.getElementById("sec1");
  const sec2 = document.getElementById("sec2");
  if (!sec1 || !sec2) return;
  
  // Validar si ya hay un participante seleccionado/creado
  const codPart = document.getElementById("currentCodPart")?.value;
  if (!codPart) {
      alert("Por favor, guarda el participante antes de continuar.");
      return;
  }

  sec1.classList.add("hidden");
  sec2.classList.remove("hidden");
  window.scrollTo({top:0, behavior:"smooth"});
}

window.volverASeccion1 = function volverASeccion1() {
  const sec1 = document.getElementById("sec1");
  const sec2 = document.getElementById("sec2");
  if (!sec1 || !sec2) return;
  sec2.classList.add("hidden");
  sec1.classList.remove("hidden");
  window.scrollTo({top:0, behavior:"smooth"});
}

/* ================= EVENT LISTENERS ================= */

document.addEventListener('DOMContentLoaded', () => {
  const addListener = (id, fn) => {
    const el = document.getElementById(id);
    if (el) el.addEventListener('click', fn);
  };

  // Botones generales
  addListener('btnForm', () => { window.location.href = 'formulario.html'; });
  addListener('btnInfo', () => alert('Búsqueda en desarrollo...'));

  // Botones Formulario
  if (document.getElementById('formView')) {
    listarParticipantes(); // Cargar tabla al inicio
    addListener('btnBackForm', () => { window.location.href = 'index.html'; });
    
    // Sección 1
    addListener('btnGuardar', guardarParticipante);
    addListener('btnNextForm', irSeccion2);

    // Sección 2
    addListener('btnGuardarSociodemo', guardarSociodemo);
    addListener('btnBackSec2', volverASeccion1);
  }

  // Lógica de Login (index.html)
  const loginForm = document.getElementById('loginForm');
  if (loginForm) {
    loginForm.addEventListener('submit', async (e) => {
      e.preventDefault();
      const userIdRaw = document.getElementById('userId')?.value;
      const pass = document.getElementById('password')?.value;
      const box = document.getElementById('msgBox');

      if (!userIdRaw || !pass) {
        if (box) { box.className = 'msg show err'; box.textContent = 'Completa ID y contraseña.'; }
        return;
      }

      try {
        const resp = await fetch(`${USER_API}/${encodeURIComponent(parseInt(userIdRaw))}`);
        if (!resp.ok) {
          if (box) { box.className = 'msg show err'; box.textContent = 'Usuario no encontrado.'; }
          return;
        }
        const usuario = await resp.json();
        const stored = usuario.password ? String(usuario.password).trim() : '';
        const entered = String(pass).trim();

        if (stored !== entered) {
          if (box) { box.className = 'msg show err'; box.textContent = 'Contraseña incorrecta.'; }
          return;
        }

        if (box) { box.className = 'msg show ok'; box.textContent = 'Ingreso correcto...'; }
        setTimeout(() => { window.location.href = 'home.html'; }, 800);

      } catch (err) {
        console.error(err);
        if (box) { box.className = 'msg show err'; box.textContent = 'Error de conexión.'; }
      }
    });
    
    addListener('btnBackLogin', () => { window.location.href = 'home.html'; });
  }
});