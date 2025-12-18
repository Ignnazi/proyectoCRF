const API_FACTOR = "http://localhost:8082/api/factor";
window.estadoFormulario = window.estadoFormulario || { codActual: null, grupoActual: null };

function msgFactor(texto, tipo = "ok") {
  const box = document.getElementById("msgBoxFactor");
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

function setTablaMsg(html) {
  const tabla = document.getElementById("tablaFactor");
  if (!tabla) return;
  tabla.innerHTML = `<tr><td colspan="13">${html}</td></tr>`;
}

function syncCodPartFactor() {
  const codEl = document.getElementById("codPartFactor");
  if (!codEl) return;

  const codState = (window.estadoFormulario?.codActual || "").trim();
  const codMain = (document.getElementById("codPart")?.value || "").trim();
  const finalCod = (codState || codMain || "").trim();

  if (finalCod && codEl.value !== finalCod) codEl.value = finalCod;
}

window.actualizarVisibilidadFactor = function () {
  const quimicos = leerRadio("quimicosFac");
  const detBlock = document.getElementById("detalleQuimicosBlock");
  if (detBlock) detBlock.style.display = quimicos === "Sí" ? "block" : "none";
  if (quimicos !== "Sí") {
    const det = document.getElementById("detalleQuimicosFac");
    if (det) det.value = "";
  }

  const fuente = leerRadio("fuenteAguaFac");
  const otraBlock = document.getElementById("otraAguaBlock");
  if (otraBlock) otraBlock.style.display = fuente === "Otra" ? "block" : "none";
  if (fuente !== "Otra") {
    const otra = document.getElementById("fuenteAguaOtraFac");
    if (otra) otra.value = "";
  }
};

function limpiarFactorForm() {
  [
    "carnesFac",
    "saladosFac",
    "frutasFac",
    "friturasFac",
    "condimentadosFac",
    "bebidasCalientesFac",
    "pesticidasFac",
    "quimicosFac",
    "humoLenaFac",
    "fuenteAguaFac",
    "tratamientoAguaFac"
  ].forEach(limpiarRadios);

  const det = document.getElementById("detalleQuimicosFac");
  if (det) det.value = "";

  const otra = document.getElementById("fuenteAguaOtraFac");
  if (otra) otra.value = "";

  window.actualizarVisibilidadFactor();
}

window.listarFactor = async function () {
  const tabla = document.getElementById("tablaFactor");
  if (!tabla) return;

  setTablaMsg("Cargando...");

  try {
    const resp = await fetch(API_FACTOR, { cache: "no-store" });

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
    lista.forEach(f => {
      const quimTxt =
        (f?.quimicos ?? "") === "Sí"
          ? `Sí${f?.detalleQuimicos ? ": " + f.detalleQuimicos : ""}`
          : (f?.quimicos ?? "");

      const aguaTxt =
        (f?.fuenteAgua ?? "") === "Otra"
          ? (f?.fuenteAguaOtra ? `Otra: ${f.fuenteAguaOtra}` : "Otra")
          : (f?.fuenteAgua ?? "");

      tabla.innerHTML += `
        <tr>
          <td>${f?.idFac ?? ""}</td>
          <td>${f?.codPart ?? ""}</td>
          <td>${f?.carnes ?? ""}</td>
          <td>${f?.salados ?? ""}</td>
          <td>${f?.frutas ?? ""}</td>
          <td>${f?.frituras ?? ""}</td>
          <td>${f?.condimentados ?? ""}</td>
          <td>${f?.bebidasCalientes ?? ""}</td>
          <td>${f?.pesticidas ?? ""}</td>
          <td>${quimTxt}</td>
          <td>${f?.humoLena ?? ""}</td>
          <td>${aguaTxt}</td>
          <td>${f?.tratamientoAgua ?? ""}</td>
        </tr>
      `;
    });
  } catch (e) {
    console.error("Factor listar error:", e);
    setTablaMsg(`No se pudo conectar a ${API_FACTOR}`);
  }
};

async function buscarFactorPorCodPart(codPart) {
  try {
    const resp = await fetch(API_FACTOR, { cache: "no-store" });
    if (!resp.ok) return null;

    const lista = await resp.json().catch(() => null);
    if (!Array.isArray(lista)) return null;

    return lista.find(x => (x?.codPart || "") === codPart) || null;
  } catch {
    return null;
  }
}

let guardandoFactor = false;

async function guardarFactor() {
  syncCodPartFactor();

  const codPart = (document.getElementById("codPartFactor")?.value || "").trim();
  if (!codPart) {
    msgFactor("Primero guarda el participante (código vacío).", "err");
    return;
  }

  const carnes = leerRadio("carnesFac");
  const salados = leerRadio("saladosFac");
  const frutas = leerRadio("frutasFac");
  const frituras = leerRadio("friturasFac");
  const condimentados = leerRadio("condimentadosFac");
  const bebidasCalientes = leerRadio("bebidasCalientesFac");
  const pesticidas = leerRadio("pesticidasFac");
  const quimicos = leerRadio("quimicosFac");
  const humoLena = leerRadio("humoLenaFac");
  const fuenteAgua = leerRadio("fuenteAguaFac");
  const tratamientoAgua = leerRadio("tratamientoAguaFac");

  const detalleQuimicos =
    quimicos === "Sí"
      ? ((document.getElementById("detalleQuimicosFac")?.value || "").trim() || null)
      : null;

  const fuenteAguaOtra =
    fuenteAgua === "Otra"
      ? ((document.getElementById("fuenteAguaOtraFac")?.value || "").trim() || null)
      : null;

  if (!carnes || !salados || !frutas || !frituras || !condimentados || !bebidasCalientes || !pesticidas || !quimicos || !humoLena || !fuenteAgua || !tratamientoAgua) {
    msgFactor("Completa todos los campos obligatorios de la sección.", "err");
    return;
  }
  if (quimicos === "Sí" && !detalleQuimicos) {
    msgFactor("Si Químicos = Sí, debes indicar ¿Cuál(es)?.", "err");
    return;
  }
  if (fuenteAgua === "Otra" && !fuenteAguaOtra) {
    msgFactor("Si Fuente de agua = Otra, debes especificar cuál.", "err");
    return;
  }

  if (guardandoFactor) return;
  guardandoFactor = true;

  try {
    const existente = await buscarFactorPorCodPart(codPart);

    const payload = {
      idFac: existente?.idFac ?? null,
      codPart,
      carnes,
      salados,
      frutas,
      frituras,
      condimentados,
      bebidasCalientes,
      pesticidas,
      quimicos,
      detalleQuimicos,
      humoLena,
      fuenteAgua,
      fuenteAguaOtra,
      tratamientoAgua
    };

    let url = API_FACTOR;
    let method = "POST";

    if (existente?.idFac != null) {
      url = `${API_FACTOR}/${encodeURIComponent(existente.idFac)}`;
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
      msgFactor(txt || `Error al guardar (${resp.status})`, "err");
      return;
    }

    msgFactor(method === "POST" ? "Factores guardados ✅" : "Factores actualizados ✅", "ok");
    await window.listarFactor();
  } catch (e) {
    console.error("Factor guardar error:", e);
    msgFactor("No se pudo conectar al backend de Factores.", "err");
  } finally {
    guardandoFactor = false;
  }
}

document.addEventListener("DOMContentLoaded", async () => {
  const tabla = document.getElementById("tablaFactor");
  if (tabla) tabla.innerHTML = `<tr><td colspan="13">JS factor cargado, consultando API...</td></tr>`;

  document.querySelectorAll("input[name='quimicosFac']").forEach(r =>
    r.addEventListener("change", window.actualizarVisibilidadFactor)
  );
  document.querySelectorAll("input[name='fuenteAguaFac']").forEach(r =>
    r.addEventListener("change", window.actualizarVisibilidadFactor)
  );

  document.getElementById("btnGuardarFactor")?.addEventListener("click", guardarFactor);
  document.getElementById("btnLimpiarFactor")?.addEventListener("click", limpiarFactorForm);

  setInterval(syncCodPartFactor, 400);

  syncCodPartFactor();
  window.actualizarVisibilidadFactor();
  await window.listarFactor();
});
