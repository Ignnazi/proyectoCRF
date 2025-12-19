// ===============================
// habito.js
// ===============================
const API_HAB = "http://localhost:8082/api/habito";
window.estadoFormulario = window.estadoFormulario || { codActual: null, grupoActual: null };

// ---------- Helpers ----------
function msgHab(idBox, texto, tipo = "ok") {
  const box = document.getElementById(idBox);
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
  const tabla = document.getElementById("tablaHabito");
  if (!tabla) return;
  tabla.innerHTML = `<tr><td colspan="9">${html}</td></tr>`;
}

function syncCodPartHabito() {
  const codEl = document.getElementById("codPartHabito");
  if (!codEl) return;

  const codState = (window.estadoFormulario?.codActual || "").trim();
  const codMain = (document.getElementById("codPart")?.value || "").trim();
  const finalCod = (codState || codMain || "").trim();

  if (finalCod && codEl.value !== finalCod) codEl.value = finalCod;
}

function mapEdadInicioToInt(v) {
  // tu entidad tiene Integer edadInicio, así que guardamos un representativo
  if (v === "<18") return 17;
  if (v === "18-25") return 21;
  if (v === ">25") return 26;
  return null;
}

// ---------- UI: visibilidad ----------
window.actualizarVisibilidadHabito = function () {
  // ===== Tabaquismo =====
  const estadoF = leerRadio("estadoFumaHab");
  const blockF = document.getElementById("fumaDetallesBlock");
  const exF = document.getElementById("fumaExBlock");

  if (blockF) {
    const show = estadoF !== "Nunca" && estadoF !== "";
    blockF.style.display = show ? "block" : "none";

    if (!show) {
      ["edadInicioFumaHab", "cantidadFumaHab", "aniosFumaHab", "tiempoDejadoFumaHab"].forEach(limpiarRadios);
    }
  }

  if (exF) {
    const showEx = estadoF === "Ex";
    exF.style.display = showEx ? "block" : "none";
    if (!showEx) limpiarRadios("tiempoDejadoFumaHab");
  }

  // ===== Alcohol =====
  const estadoB = leerRadio("estadoBeberHab");
  const blockB = document.getElementById("beberDetallesBlock");
  const exB = document.getElementById("beberExBlock");

  if (blockB) {
    const show = estadoB !== "Nunca" && estadoB !== "";
    blockB.style.display = show ? "block" : "none";

    if (!show) {
      ["frecuenciaBeberHab", "cantidadBeberHab", "aniosBeberHab", "tiempoDejadoBeberHab"].forEach(limpiarRadios);
    }
  }

  if (exB) {
    const showEx = estadoB === "Ex";
    exB.style.display = showEx ? "block" : "none";
    if (!showEx) limpiarRadios("tiempoDejadoBeberHab");
  }
};

function limpiarFuma() {
  limpiarRadios("estadoFumaHab");
  ["edadInicioFumaHab", "cantidadFumaHab", "aniosFumaHab", "tiempoDejadoFumaHab"].forEach(limpiarRadios);
  window.actualizarVisibilidadHabito();
}

function limpiarBeber() {
  limpiarRadios("estadoBeberHab");
  ["frecuenciaBeberHab", "cantidadBeberHab", "aniosBeberHab", "tiempoDejadoBeberHab"].forEach(limpiarRadios);
  window.actualizarVisibilidadHabito();
}

// ---------- API ----------
window.listarHabito = async function () {
  const tabla = document.getElementById("tablaHabito");
  if (!tabla) return;

  setTablaMsg("Cargando...");

  try {
    const resp = await fetch(API_HAB, { cache: "no-store" });
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
    lista.forEach(h => {
      tabla.innerHTML += `
        <tr>
          <td>${h?.idHabit ?? ""}</td>
          <td>${h?.codPart ?? ""}</td>
          <td>${h?.tipo ?? ""}</td>
          <td>${h?.estado ?? ""}</td>
          <td>${h?.edadInicio ?? ""}</td>
          <td>${h?.frecuencia ?? ""}</td>
          <td>${h?.cantidad ?? ""}</td>
          <td>${h?.aniosConsumo ?? ""}</td>
          <td>${h?.tiempoDejado ?? ""}</td>
        </tr>
      `;
    });
  } catch (e) {
    console.error("Habito listar error:", e);
    setTablaMsg(`No se pudo conectar a ${API_HAB}`);
  }
};

async function buscarHabitoPorCodPartTipo(codPart, tipo) {
  try {
    const resp = await fetch(API_HAB, { cache: "no-store" });
    if (!resp.ok) return null;
    const lista = await resp.json().catch(() => null);
    if (!Array.isArray(lista)) return null;
    return lista.find(h => (h?.codPart || "") === codPart && (h?.tipo || "") === tipo) || null;
  } catch {
    return null;
  }
}

let guardandoF = false;
async function guardarFuma() {
  syncCodPartHabito();
  const codPart = (document.getElementById("codPartHabito")?.value || "").trim();
  if (!codPart) {
    msgHab("msgBoxHabitoFuma", "Primero guarda el participante (código vacío).", "err");
    return;
  }

  const estado = leerRadio("estadoFumaHab"); // Nunca / Ex / Actual
  if (!estado) {
    msgHab("msgBoxHabitoFuma", "Selecciona estado de tabaquismo.", "err");
    return;
  }

  // Si Nunca → guardar solo estado
  let edadInicio = null, cantidad = null, aniosConsumo = null, tiempoDejado = null;

  if (estado !== "Nunca") {
    const edadCat = leerRadio("edadInicioFumaHab");
    const cant = leerRadio("cantidadFumaHab");
    const anios = leerRadio("aniosFumaHab");

    edadInicio = mapEdadInicioToInt(edadCat);
    cantidad = cant || null;
    aniosConsumo = anios || null;

    if (!edadInicio || !cantidad || !aniosConsumo) {
      msgHab("msgBoxHabitoFuma", "Completa: edad de inicio, cantidad promedio y tiempo total fumando.", "err");
      return;
    }

    if (estado === "Ex") {
      const t = leerRadio("tiempoDejadoFumaHab");
      if (!t) {
        msgHab("msgBoxHabitoFuma", "Si es Exfumador, indica el tiempo desde que dejó de fumar.", "err");
        return;
      }
      tiempoDejado = t;
    }
  }

  if (guardandoF) return;
  guardandoF = true;

  try {
    const existente = await buscarHabitoPorCodPartTipo(codPart, "Fumar");

    const payload = {
      idHabit: existente?.idHabit ?? null,
      codPart,
      tipo: "Fumar",
      estado,                // Nunca / Ex / Actual
      frecuencia: null,      // tabaquismo no usa frecuencia en tu formulario
      cantidad,
      aniosConsumo,
      tiempoDejado,
      edadInicio
    };

    let url = API_HAB;
    let method = "POST";
    if (existente?.idHabit != null) {
      url = `${API_HAB}/${encodeURIComponent(existente.idHabit)}`;
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
      msgHab("msgBoxHabitoFuma", txt || `Error al guardar (${resp.status})`, "err");
      return;
    }

    // POST/PUT en tu backend es void → NO resp.json() :contentReference[oaicite:5]{index=5}
    msgHab("msgBoxHabitoFuma", method === "POST" ? "Tabaquismo guardado ✅" : "Tabaquismo actualizado ✅", "ok");
    await window.listarHabito();
  } catch (e) {
    console.error("Habito fumar guardar error:", e);
    msgHab("msgBoxHabitoFuma", "No se pudo conectar al backend de hábitos.", "err");
  } finally {
    guardandoF = false;
  }
}

let guardandoB = false;
async function guardarBeber() {
  syncCodPartHabito();
  const codPart = (document.getElementById("codPartHabito")?.value || "").trim();
  if (!codPart) {
    msgHab("msgBoxHabitoBeber", "Primero guarda el participante (código vacío).", "err");
    return;
  }

  const estado = leerRadio("estadoBeberHab"); // Nunca / Ex / Actual
  if (!estado) {
    msgHab("msgBoxHabitoBeber", "Selecciona estado de consumo.", "err");
    return;
  }

  let frecuencia = null, cantidad = null, aniosConsumo = null, tiempoDejado = null;

  if (estado !== "Nunca") {
    frecuencia = leerRadio("frecuenciaBeberHab") || null;
    cantidad = leerRadio("cantidadBeberHab") || null;
    aniosConsumo = leerRadio("aniosBeberHab") || null;

    if (!frecuencia || !cantidad || !aniosConsumo) {
      msgHab("msgBoxHabitoBeber", "Completa: frecuencia, cantidad típica y años de consumo habitual.", "err");
      return;
    }

    if (estado === "Ex") {
      const t = leerRadio("tiempoDejadoBeberHab");
      if (!t) {
        msgHab("msgBoxHabitoBeber", "Si es Exconsumidor, indica el tiempo desde que dejó de beber regularmente.", "err");
        return;
      }
      tiempoDejado = t;
    }
  }

  if (guardandoB) return;
  guardandoB = true;

  try {
    const existente = await buscarHabitoPorCodPartTipo(codPart, "Beber");

    const payload = {
      idHabit: existente?.idHabit ?? null,
      codPart,
      tipo: "Beber",
      estado,        // Nunca / Ex / Actual
      frecuencia,
      cantidad,
      aniosConsumo,
      tiempoDejado,
      edadInicio: null
    };

    let url = API_HAB;
    let method = "POST";
    if (existente?.idHabit != null) {
      url = `${API_HAB}/${encodeURIComponent(existente.idHabit)}`;
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
      msgHab("msgBoxHabitoBeber", txt || `Error al guardar (${resp.status})`, "err");
      return;
    }

    msgHab("msgBoxHabitoBeber", method === "POST" ? "Alcohol guardado ✅" : "Alcohol actualizado ✅", "ok");
    await window.listarHabito();
  } catch (e) {
    console.error("Habito beber guardar error:", e);
    msgHab("msgBoxHabitoBeber", "No se pudo conectar al backend de hábitos.", "err");
  } finally {
    guardandoB = false;
  }
}

// ---------- Init ----------
document.addEventListener("DOMContentLoaded", async () => {
  // marca visual: si NO aparece, el JS no cargó
  const tabla = document.getElementById("tablaHabito");
  if (tabla) tabla.innerHTML = `<tr><td colspan="9">JS habito cargado, consultando API...</td></tr>`;

  // listeners visibilidad
  document.querySelectorAll("input[name='estadoFumaHab']").forEach(r => r.addEventListener("change", window.actualizarVisibilidadHabito));
  document.querySelectorAll("input[name='estadoBeberHab']").forEach(r => r.addEventListener("change", window.actualizarVisibilidadHabito));

  // botones
  const btnGF = document.getElementById("btnGuardarFumaHab");
  const btnLF = document.getElementById("btnLimpiarFumaHab");
  const btnGB = document.getElementById("btnGuardarBeberHab");
  const btnLB = document.getElementById("btnLimpiarBeberHab");

  if (btnGF) btnGF.addEventListener("click", guardarFuma);
  if (btnLF) btnLF.addEventListener("click", limpiarFuma);
  if (btnGB) btnGB.addEventListener("click", guardarBeber);
  if (btnLB) btnLB.addEventListener("click", limpiarBeber);

  // sync codPart constante (sin tocar participante.js)
  setInterval(syncCodPartHabito, 400);

  syncCodPartHabito();
  window.actualizarVisibilidadHabito();
  await window.listarHabito();
});
