// ===============================
// helicobacter.js
// ===============================
const API_HELIC = "http://localhost:8080/api/helicobacter";

window.estadoFormulario = window.estadoFormulario || { codActual: null, grupoActual: null };

// ---------- Helpers ----------
function msgHelic(texto, tipo = "ok") {
  const box = document.getElementById("msgBoxHelic");
  if (!box) return;
  box.className = "msg show " + (tipo === "err" ? "err" : "ok");
  box.textContent = texto;
  setTimeout(() => box.classList.remove("show"), 3500);
}

function leerRadio(name) {
  return document.querySelector(`input[name='${name}']:checked`)?.value || "";
}

function leerChecks(name) {
  return Array.from(document.querySelectorAll(`input[name='${name}']:checked`)).map(x => x.value);
}

function setCheckedRadio(name, val) {
  document.querySelectorAll(`input[name='${name}']`).forEach(r => (r.checked = (r.value === val)));
}

function setCheckedChecks(name, valuesArr) {
  const set = new Set(valuesArr || []);
  document.querySelectorAll(`input[name='${name}']`).forEach(c => (c.checked = set.has(c.value)));
}

// ---------- UI: visibilidad ----------
window.actualizarVisibilidadHelicobacter = function () {
  const resultado = leerRadio("resultadoHelic");

  // Si resultado es Negativo o Desconocido, mostrar bloque de preguntas extra
  const bloqueNegDes = document.getElementById("negDesBlock");
  if (bloqueNegDes) bloqueNegDes.style.display = (resultado === "Negativo" || resultado === "Desconocido") ? "block" : "none";

  // Pasado positivo detalle solo si "Sí"
  const pasado = leerRadio("pasadoPositivoHelic");
  const pasadoDet = document.getElementById("pasadoDetalleBlock");
  if (pasadoDet) pasadoDet.style.display = (pasado === "Sí") ? "block" : "none";
  if (pasado !== "Sí") {
    const el = document.getElementById("pasadoDetalleHelic");
    if (el) el.value = "";
  }

  // Tratamiento detalle solo si "Sí"
  const trat = leerRadio("tratamientoHelic");
  const tratDet = document.getElementById("tratDetalleBlock");
  if (tratDet) tratDet.style.display = (trat === "Sí") ? "block" : "none";
  if (trat !== "Sí") {
    const el = document.getElementById("tratamientoDetalleHelic");
    if (el) el.value = "";
  }

  // Tipo examen: mostrar "Otro" si lo marcaron
  const tipos = leerChecks("tipoExamenHelic");
  const otroBlock = document.getElementById("otroExamenBlock");
  if (otroBlock) otroBlock.style.display = tipos.includes("Otro") ? "block" : "none";
  if (!tipos.includes("Otro")) {
    const el = document.getElementById("otroExamenHelic");
    if (el) el.value = "";
  }

  // Repetido: si Sí, mostrar fecha+resultado
  const rep = leerRadio("repetidoHelic");
  const repBlock = document.getElementById("repetidoDetalleBlock");
  if (repBlock) repBlock.style.display = (rep === "Sí") ? "block" : "none";
  if (rep !== "Sí") {
    const f = document.getElementById("repetidoFechaHelic");
    const r = document.getElementById("repetidoResultadoHelic");
    if (f) f.value = "";
    if (r) r.value = "";
  }
};

function limpiarHelicForm() {
  document.querySelectorAll("input[name='resultadoHelic']").forEach(r => (r.checked = false));
  document.querySelectorAll("input[name='pasadoPositivoHelic']").forEach(r => (r.checked = false));
  document.querySelectorAll("input[name='tratamientoHelic']").forEach(r => (r.checked = false));
  document.querySelectorAll("input[name='antiguedadHelic']").forEach(r => (r.checked = false));
  document.querySelectorAll("input[name='usoIbpAbxHelic']").forEach(r => (r.checked = false));
  document.querySelectorAll("input[name='repetidoHelic']").forEach(r => (r.checked = false));
  document.querySelectorAll("input[name='tipoExamenHelic']").forEach(c => (c.checked = false));

  ["pasadoDetalleHelic", "tratamientoDetalleHelic", "otroExamenHelic"].forEach(id => {
    const el = document.getElementById(id);
    if (el) el.value = "";
  });

  const f = document.getElementById("repetidoFechaHelic");
  const rr = document.getElementById("repetidoResultadoHelic");
  if (f) f.value = "";
  if (rr) rr.value = "";

  window.actualizarVisibilidadHelicobacter();
}

// Exponer globalmente para el botón de nuevo participante
window.limpiarHelicobacterForm = limpiarHelicForm;

// ---------- API ----------
window.listarHelicobacter = async function () {
  const tabla = document.getElementById("tablaHelicobacter");
  if (!tabla) return;

  tabla.innerHTML = `<tr><td colspan="5">Cargando...</td></tr>`;

  try {
    const resp = await fetch(API_HELIC);
    if (!resp.ok) {
      const txt = await resp.text().catch(() => "");
      tabla.innerHTML = `<tr><td colspan="5">Error API (${resp.status}): ${txt || "sin detalle"}</td></tr>`;
      return;
    }

    const lista = await resp.json().catch(() => null);
    if (!Array.isArray(lista) || lista.length === 0) {
      tabla.innerHTML = `<tr><td colspan="5">Sin registros</td></tr>`;
      return;
    }

    tabla.innerHTML = "";
    lista.forEach(h => {
      tabla.innerHTML += `
        <tr>
          <td>${h?.idHelic ?? ""}</td>
          <td>${h?.codPart ?? ""}</td>
          <td>${h?.resultadoExam ?? ""}</td>
          <td>${(h?.tipoExamen || "").toString().replaceAll("|", ", ")}</td>
          <td>${h?.antiguedad ?? ""}</td>
        </tr>
      `;
    });
  } catch (e) {
    console.error("Helicobacter listar error:", e);
    tabla.innerHTML = `<tr><td colspan="5">No se pudo conectar a ${API_HELIC}</td></tr>`;
  }
};

async function buscarHelicPorCodPart(codPart) {
  try {
    const resp = await fetch(`${API_HELIC}/por-participante/${encodeURIComponent(codPart)}`);
    if (!resp.ok) return null; // 404 -> null
    return await resp.json();
  } catch {
    return null;
  }
}

let guardandoHelic = false;

async function guardarHelicobacter() {
  const codPart = (document.getElementById("codPartHelic")?.value || "").trim();
  if (!codPart) {
    msgHelic("Primero guarda el participante (código vacío).", "err");
    return;
  }

  const resultadoExam = leerRadio("resultadoHelic");
  if (!resultadoExam) {
    msgHelic("Debes seleccionar el resultado del examen.", "err");
    return;
  }

  const antiguedad = leerRadio("antiguedadHelic");
  if (!antiguedad) {
    msgHelic("Debes seleccionar la antigüedad del test.", "err");
    return;
  }

  // Tipo(s) examen (múltiple)
  const tiposArr = leerChecks("tipoExamenHelic");
  if (!tiposArr.length) {
    msgHelic("Debes seleccionar al menos un tipo de examen realizado.", "err");
    return;
  }

  const tipoExamen = tiposArr.join("|");
  const otroExamen = tiposArr.includes("Otro")
    ? ((document.getElementById("otroExamenHelic")?.value || "").trim() || null)
    : null;

  if (tiposArr.includes("Otro") && !otroExamen) {
    msgHelic("Si seleccionas 'Otro', debes especificar cuál.", "err");
    return;
  }

  // Bloque Negativo/Desconocido
  let pasadoPositivo = null;
  let pasadoDetalle = null;
  let tratamiento = null;
  let tratamientoDetalle = null;

  if (resultadoExam === "Negativo" || resultadoExam === "Desconocido") {
    pasadoPositivo = leerRadio("pasadoPositivoHelic");
    tratamiento = leerRadio("tratamientoHelic");

    if (!pasadoPositivo) {
      msgHelic("Debes responder si tuvo un resultado positivo en el pasado.", "err");
      return;
    }
    if (!tratamiento) {
      msgHelic("Debes responder si recibió tratamiento de erradicación.", "err");
      return;
    }

    if (pasadoPositivo === "Sí") {
      pasadoDetalle = (document.getElementById("pasadoDetalleHelic")?.value || "").trim() || null;
      if (!pasadoDetalle) {
        msgHelic("Si respondes 'Sí' (pasado positivo), indica año aprox. y tipo de examen.", "err");
        return;
      }
    }

    if (tratamiento === "Sí") {
      tratamientoDetalle = (document.getElementById("tratamientoDetalleHelic")?.value || "").trim() || null;
      if (!tratamientoDetalle) {
        msgHelic("Si respondes 'Sí' (tratamiento), indica año y esquema si se conoce.", "err");
        return;
      }
    }
  }

  const usoIbpAbx = leerRadio("usoIbpAbxHelic");
  if (!usoIbpAbx) {
    msgHelic("Debes responder uso de antibióticos/IBP en las 4 semanas previas.", "err");
    return;
  }

  const repetido = leerRadio("repetidoHelic");
  if (!repetido) {
    msgHelic("Debes responder si repitió el examen posteriormente.", "err");
    return;
  }

  let repetidoFecha = null;
  let repetidoResultado = null;

  if (repetido === "Sí") {
    const f = (document.getElementById("repetidoFechaHelic")?.value || "").trim();
    repetidoFecha = f || null;
    repetidoResultado = (document.getElementById("repetidoResultadoHelic")?.value || "").trim() || null;

    if (!repetidoFecha || !repetidoResultado) {
      msgHelic("Si repitió el examen, debes indicar fecha y resultado del más reciente.", "err");
      return;
    }
  }

  if (guardandoHelic) return;
  guardandoHelic = true;

  try {
    const existente = await buscarHelicPorCodPart(codPart);

    const payload = {
      idHelic: existente?.idHelic ?? null,
      codPart,

      resultadoExam,
      pasadoPositivo,
      pasadoDetalle,
      tratamiento,
      tratamientoDetalle,

      tipoExamen,
      otroExamen,
      antiguedad,

      usoIbpAbx,
      repetido,
      repetidoFecha,
      repetidoResultado
    };

    let url = API_HELIC;
    let method = "POST";

    if (existente?.idHelic != null) {
      url = `${API_HELIC}/${encodeURIComponent(existente.idHelic)}`;
      method = "PUT";
    }

    const resp = await fetch(url, {
      method,
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });

    if (!resp.ok) {
      const txt = await resp.text().catch(() => "");
      msgHelic(txt || `Error al guardar (${resp.status})`, "err");
      return;
    }

    await resp.json().catch(() => null);

    msgHelic(method === "POST" ? "Helicobacter guardado ✅" : "Helicobacter actualizado ✅", "ok");
    await window.listarHelicobacter();
  } catch (e) {
    console.error("Helicobacter guardar error:", e);
    msgHelic("No se pudo conectar al backend de Helicobacter.", "err");
  } finally {
    guardandoHelic = false;
  }
}

// ---------- Init ----------
document.addEventListener("DOMContentLoaded", async () => {
  const tabla = document.getElementById("tablaHelicobacter");
  if (tabla) {
    tabla.innerHTML = `<tr><td colspan="5">JS helicobacter cargado, consultando API...</td></tr>`;
  }

  const btnGuardar = document.getElementById("btnGuardarHelic");
  const btnLimpiar = document.getElementById("btnLimpiarHelic");

  if (btnGuardar) btnGuardar.addEventListener("click", guardarHelicobacter);
  if (btnLimpiar) btnLimpiar.addEventListener("click", limpiarHelicForm);

  // listeners de UI
  document.querySelectorAll("input[name='resultadoHelic']").forEach(r => r.addEventListener("change", window.actualizarVisibilidadHelicobacter));
  document.querySelectorAll("input[name='pasadoPositivoHelic']").forEach(r => r.addEventListener("change", window.actualizarVisibilidadHelicobacter));
  document.querySelectorAll("input[name='tratamientoHelic']").forEach(r => r.addEventListener("change", window.actualizarVisibilidadHelicobacter));
  document.querySelectorAll("input[name='tipoExamenHelic']").forEach(c => c.addEventListener("change", window.actualizarVisibilidadHelicobacter));
  document.querySelectorAll("input[name='repetidoHelic']").forEach(r => r.addEventListener("change", window.actualizarVisibilidadHelicobacter));

  window.actualizarVisibilidadHelicobacter();
  await window.listarHelicobacter();
});
