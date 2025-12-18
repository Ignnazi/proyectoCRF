// ===============================
// histopatologia.js
// ===============================
const API_HISTO = "http://localhost:8082/api/histopatologia";

// Reusa estado global (igual que tus otras secciones)
window.estadoFormulario = window.estadoFormulario || { codActual: null, grupoActual: null };

// ---------- Helpers ----------
function msgHisto(texto, tipo = "ok") {
  const box = document.getElementById("msgBoxHisto");
  if (!box) return;
  box.className = "msg show " + (tipo === "err" ? "err" : "ok");
  box.textContent = texto;
  setTimeout(() => box.classList.remove("show"), 3500);
}

function leerRadio(name) {
  return document.querySelector(`input[name='${name}']:checked`)?.value || "";
}

function limpiarRadios(name) {
  document.querySelectorAll(`input[name='${name}']`).forEach(r => (r.checked = false));
}

function esCasoActual() {
  const grupo =
    window.estadoFormulario?.grupoActual ||
    document.querySelector("input[name='grupo']:checked")?.value ||
    "";
  return String(grupo).toLowerCase() === "caso";
}

function syncCodPartHisto() {
  const codEl = document.getElementById("codPartHisto");
  if (!codEl) return;

  const codState = (window.estadoFormulario?.codActual || "").trim();
  const codMain = (document.getElementById("codPart")?.value || "").trim();
  const finalCod = (codState || codMain || "").trim();

  if (finalCod && codEl.value !== finalCod) codEl.value = finalCod;
}

// ---------- UI: visibilidad (SOLO CASOS) ----------
window.actualizarVisibilidadHistopatologia = function () {
  const sec = document.getElementById("sec9");                 // sección completa
  const block = document.getElementById("histoSoloCasosBlock"); // inputs
  const aviso = document.getElementById("soloCasosHistoMsg");

  // Caso si radio grupo = Caso (o estado global si ya lo seteaste)
  const grupoSel =
    document.querySelector("input[name='grupo']:checked")?.value ||
    window.estadoFormulario?.grupoActual ||
    "";

  const caso = String(grupoSel).toLowerCase() === "caso";

  // ✅ Lo importante: ocultar/mostrar la SECCIÓN COMPLETA
  if (sec) sec.style.display = caso ? "block" : "none";

  // Si es caso, mostrar inputs (normalmente sí)
  if (block) block.style.display = caso ? "block" : "none";

  // No necesitamos aviso si la sección está oculta, pero por si acaso:
  if (aviso) {
    aviso.className = "msg";
    aviso.textContent = "";
  }

  // Si NO es caso, limpiar radios para que no queden valores “fantasma”
  if (!caso) {
    ["tipoHisto", "localizacionHisto", "estadioHisto"].forEach(name => {
      document.querySelectorAll(`input[name='${name}']`).forEach(r => (r.checked = false));
    });
  }
};


function limpiarHistoForm() {
  ["tipoHisto", "localizacionHisto", "estadioHisto"].forEach(limpiarRadios);
  window.actualizarVisibilidadHistopatologia();
}

// ---------- API ----------
window.listarHistopatologia = async function () {
  const tabla = document.getElementById("tablaHistopatologia");
  if (!tabla) return;

  tabla.innerHTML = `<tr><td colspan="5">Cargando...</td></tr>`;

  try {
    const resp = await fetch(API_HISTO, { cache: "no-store" });

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
          <td>${h?.idHisto ?? ""}</td>
          <td>${h?.codPart ?? ""}</td>
          <td>${h?.tipo ?? ""}</td>
          <td>${h?.localizacion ?? ""}</td>
          <td>${h?.estadio ?? ""}</td>
        </tr>
      `;
    });
  } catch (e) {
    console.error("Histopatologia listar error:", e);
    tabla.innerHTML = `<tr><td colspan="5">No se pudo conectar a ${API_HISTO}</td></tr>`;
  }
};

async function buscarHistoPorCodPart(codPart) {
  // 1) endpoint esperado
  try {
    const r = await fetch(`${API_HISTO}/por-participante/${encodeURIComponent(codPart)}`, { cache: "no-store" });
    if (r.ok) return await r.json();
  } catch {}

  // 2) fallback: listar y buscar
  try {
    const r2 = await fetch(API_HISTO, { cache: "no-store" });
    if (!r2.ok) return null;
    const lista = await r2.json().catch(() => null);
    if (!Array.isArray(lista)) return null;
    return lista.find(x => (x?.codPart || "") === codPart) || null;
  } catch {
    return null;
  }
}

let guardandoHisto = false;

async function guardarHistopatologia() {
  syncCodPartHisto();

  const codPart = (document.getElementById("codPartHisto")?.value || "").trim();
  if (!codPart) {
    msgHisto("Primero guarda el participante (código vacío).", "err");
    return;
  }

  // Regla clave: SOLO CASOS
  if (!esCasoActual()) {
    msgHisto("Histopatología solo se puede guardar en CASO.", "err");
    return;
  }

  const tipo = leerRadio("tipoHisto");
  const localizacion = leerRadio("localizacionHisto");
  const estadio = leerRadio("estadioHisto");

  if (!tipo || !localizacion || !estadio) {
    msgHisto("Completa: Tipo, Localización y Estadio.", "err");
    return;
  }

  if (guardandoHisto) return;
  guardandoHisto = true;

  try {
    const existente = await buscarHistoPorCodPart(codPart);

    const payload = {
      idHisto: existente?.idHisto ?? null,
      codPart,
      tipo,
      localizacion,
      estadio
    };

    let url = API_HISTO;
    let method = "POST";

    if (existente?.idHisto != null) {
      url = `${API_HISTO}/${encodeURIComponent(existente.idHisto)}`;
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
      msgHisto(txt || `Error al guardar (${resp.status})`, "err");
      return;
    }

    msgHisto(method === "POST" ? "Histopatología guardada ✅" : "Histopatología actualizada ✅", "ok");
    await window.listarHistopatologia();
  } catch (e) {
    console.error("Histopatologia guardar error:", e);
    msgHisto("No se pudo conectar al backend de Histopatología.", "err");
  } finally {
    guardandoHisto = false;
  }
}

// ---------- Init ----------
document.addEventListener("DOMContentLoaded", async () => {
  const tabla = document.getElementById("tablaHistopatologia");
  if (tabla) tabla.innerHTML = `<tr><td colspan="5">JS histopatologia cargado, consultando API...</td></tr>`;

  const btnGuardar = document.getElementById("btnGuardarHisto");
  const btnLimpiar = document.getElementById("btnLimpiarHisto");

  if (btnGuardar) btnGuardar.addEventListener("click", guardarHistopatologia);
  if (btnLimpiar) btnLimpiar.addEventListener("click", limpiarHistoForm);

  document.querySelectorAll("input[name='grupo']").forEach(r => {
    r.addEventListener("change", window.actualizarVisibilidadHistopatologia);
  });

  window.actualizarVisibilidadHistopatologia();
  await window.listarHistopatologia();
});
