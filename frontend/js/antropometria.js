// ===============================
// antropometria.js
// ===============================
const API_ANTROP = "http://localhost:8080/api/antropometria";

window.estadoFormulario = window.estadoFormulario || { codActual: null, grupoActual: null };

// ---------- Helpers ----------
function msgAntrop(texto, tipo = "ok") {
  const box = document.getElementById("msgBoxAntrop");
  if (!box) return;
  box.className = "msg show " + (tipo === "err" ? "err" : "ok");
  box.textContent = texto;
  setTimeout(() => box.classList.remove("show"), 3500);
}

function setTablaMsg(html) {
  const tabla = document.getElementById("tablaAntropometria");
  if (!tabla) return;
  tabla.innerHTML = `<tr><td colspan="5">${html}</td></tr>`;
}

function num(v) {
  const x = parseFloat((v ?? "").toString().trim().replace(",", "."));
  return Number.isFinite(x) ? x : null;
}

function round1(x) {
  return Math.round(x * 10) / 10;
}

function calcImc(peso, estatura) {
  if (peso == null || estatura == null) return "";
  if (peso <= 0 || estatura <= 0) return "";
  return String(round1(peso / (estatura * estatura)));
}

function actualizarImcUI() {
  const imcEl = document.getElementById("imcAntrop");
  if (!imcEl) return;

  const peso = num(document.getElementById("pesoAntrop")?.value);
  const est = num(document.getElementById("estaturaAntrop")?.value);

  imcEl.value = calcImc(peso, est);
}

function syncCodPartAntrop() {
  const codEl = document.getElementById("codPartAntrop");
  if (!codEl) return;

  // 1) lo que setea participante.js (si está)
  const codState = (window.estadoFormulario?.codActual || "").trim();
  // 2) respaldo: campo codPart sección 1
  const codMain = (document.getElementById("codPart")?.value || "").trim();

  const finalCod = (codState || codMain || "").trim();
  if (finalCod && codEl.value !== finalCod) codEl.value = finalCod;
}

// ---------- API ----------
window.listarAntropometria = async function () {
  const tabla = document.getElementById("tablaAntropometria");
  if (!tabla) return;

  setTablaMsg("Cargando...");

  try {
    const resp = await fetch(API_ANTROP, { cache: "no-store" });

    if (!resp.ok) {
      const txt = await resp.text().catch(() => "");
      setTablaMsg(`Error API (${resp.status}): ${txt || "sin detalle"}`);
      return;
    }

    const lista = await resp.json().catch(() => null);

    if (!Array.isArray(lista) || lista.length === 0) {
      setTablaMsg("Sin registros");
      return;
    }

    tabla.innerHTML = "";
    lista.forEach(a => {
      tabla.innerHTML += `
        <tr>
          <td>${a?.idAntrop ?? ""}</td>
          <td>${a?.codPart ?? ""}</td>
          <td>${a?.peso ?? ""}</td>
          <td>${a?.estatura ?? ""}</td>
          <td>${a?.imc ?? ""}</td>
        </tr>
      `;
    });
  } catch (e) {
    console.error("Antropometria listar error:", e);
    setTablaMsg(`No se pudo conectar a ${API_ANTROP}`);
  }
};

async function buscarAntropometriaPorCodPart(codPart) {
  try {
    const resp = await fetch(
      `${API_ANTROP}/por-participante/${encodeURIComponent(codPart)}`,
      { cache: "no-store" }
    );
    if (!resp.ok) return null;

    const data = await resp.json().catch(() => null);
    if (!data) return null;

    // tu backend devuelve LISTA
    if (Array.isArray(data)) {
      if (data.length === 0) return null;
      const ordenada = [...data].sort((a, b) => (a?.idAntrop ?? 0) - (b?.idAntrop ?? 0));
      return ordenada[ordenada.length - 1] || null;
    }

    // por si en algún momento te devuelve objeto
    return data;
  } catch {
    return null;
  }
}

let guardandoAntrop = false;

async function guardarAntropometria() {
  syncCodPartAntrop();

  const codPart = (document.getElementById("codPartAntrop")?.value || "").trim();
  if (!codPart) {
    msgAntrop("Primero guarda el participante (código vacío).", "err");
    return;
  }

  const peso = num(document.getElementById("pesoAntrop")?.value);
  const estatura = num(document.getElementById("estaturaAntrop")?.value);

  if (peso == null || peso <= 0) {
    msgAntrop("Peso inválido.", "err");
    return;
  }
  if (estatura == null || estatura <= 0) {
    msgAntrop("Estatura inválida.", "err");
    return;
  }

  // IMC automático
  actualizarImcUI();

  if (guardandoAntrop) return;
  guardandoAntrop = true;

  try {
    const existente = await buscarAntropometriaPorCodPart(codPart);

    // Backend calcula IMC; enviamos lo necesario
    const payload = { codPart, peso, estatura };

    let url = API_ANTROP;
    let method = "POST";

    if (existente?.idAntrop != null) {
      url = `${API_ANTROP}/${encodeURIComponent(existente.idAntrop)}`;
      method = "PUT";
    }

    const resp = await fetch(url, {
      method,
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
      cache: "no-store"
    });

    if (!resp.ok) {
      const txt = await resp.text().catch(() => "");
      msgAntrop(txt || `Error al guardar (${resp.status})`, "err");
      return;
    }

    await resp.json().catch(() => null);

    msgAntrop(method === "POST" ? "Antropometría guardada ✅" : "Antropometría actualizada ✅", "ok");
    await window.listarAntropometria();
  } catch (e) {
    console.error("Antropometria guardar error:", e);
    msgAntrop("No se pudo conectar al backend de Antropometría.", "err");
  } finally {
    guardandoAntrop = false;
  }
}

function limpiarAntropometriaForm() {
  const pesoEl = document.getElementById("pesoAntrop");
  const estEl = document.getElementById("estaturaAntrop");
  const imcEl = document.getElementById("imcAntrop");

  if (pesoEl) pesoEl.value = "";
  if (estEl) estEl.value = "";
  if (imcEl) imcEl.value = "";

  syncCodPartAntrop();
}

// Exponer globalmente para el botón de nuevo participante
window.limpiarAntropometriaForm = limpiarAntropometriaForm;

// ---------- Init ----------
document.addEventListener("DOMContentLoaded", async () => {
  const tabla = document.getElementById("tablaAntropometria");
  if (tabla) {
    // Si esto NO aparece, el archivo NO está cargando
    tabla.innerHTML = `<tr><td colspan="5">JS antropometria cargado, consultando API...</td></tr>`;
  }

  const btnGuardar = document.getElementById("btnGuardarAntrop");
  const btnLimpiar = document.getElementById("btnLimpiarAntrop");

  if (btnGuardar) btnGuardar.addEventListener("click", guardarAntropometria);
  if (btnLimpiar) btnLimpiar.addEventListener("click", limpiarAntropometriaForm);

  const pesoEl = document.getElementById("pesoAntrop");
  const estEl = document.getElementById("estaturaAntrop");
  if (pesoEl) pesoEl.addEventListener("input", actualizarImcUI);
  if (estEl) estEl.addEventListener("input", actualizarImcUI);

  // Mantener cod sincronizado (igual estilo que el resto)
  setInterval(syncCodPartAntrop, 400);

  syncCodPartAntrop();
  actualizarImcUI();
  await window.listarAntropometria();
});
