// ===============================
// antecedente.js
// ===============================
const API_ANTEC = "http://localhost:8082/api/antecedente";

window.estadoFormulario = window.estadoFormulario || { codActual: null, grupoActual: null };

// ---------- Helpers ----------
function msgAntec(texto, tipo = "ok") {
  const box = document.getElementById("msgBoxAntec");
  if (!box) return;
  box.className = "msg show " + (tipo === "err" ? "err" : "ok");
  box.textContent = texto;
  setTimeout(() => box.classList.remove("show"), 3500);
}

function leerRadio(name) {
  return document.querySelector(`input[name='${name}']:checked`)?.value || "";
}

function esCasoActual() {
  const grupo =
    window.estadoFormulario?.grupoActual ||
    document.querySelector("input[name='grupo']:checked")?.value ||
    "";
  return String(grupo).toLowerCase() === "caso";
}

// ---------- UI: visibilidad ----------
window.actualizarVisibilidadAntecedente = function () {
  // Solo casos
  const solo = document.getElementById("soloCasosBlock");
  if (solo) {
    const caso = esCasoActual();
    solo.style.display = caso ? "block" : "none";

    // Si es Control, limpiar y evitar que se envíe
    if (!caso) {
      document.querySelectorAll("input[name='diagnosticoAntec']").forEach(r => (r.checked = false));
      const fecha = document.getElementById("fechaDiagAntec");
      if (fecha) fecha.value = "";
    }
  }

  // “¿Cuál(es)?” solo si famOtro = Sí
  const cualBlock = document.getElementById("cualCancerBlock");
  const famOtro = leerRadio("famOtroAntec");
  if (cualBlock) cualBlock.style.display = famOtro === "Sí" ? "block" : "none";

  if (famOtro !== "Sí") {
    const otro = document.getElementById("otroCancerAntec");
    if (otro) otro.value = "";
  }
};

function limpiarAntecedenteForm() {
  document.querySelectorAll("input[name='diagnosticoAntec']").forEach(r => (r.checked = false));
  document.querySelectorAll("input[name='famCgAntec']").forEach(r => (r.checked = false));
  document.querySelectorAll("input[name='famOtroAntec']").forEach(r => (r.checked = false));
  document.querySelectorAll("input[name='cirugiaAntec']").forEach(r => (r.checked = false));

  const fecha = document.getElementById("fechaDiagAntec");
  if (fecha) fecha.value = "";

  ["otroCancerAntec", "otrasEnfAntec", "medicamentosAntec"].forEach(id => {
    const el = document.getElementById(id);
    if (el) el.value = "";
  });

  window.actualizarVisibilidadAntecedente();
}

// ---------- API ----------
window.listarAntecedente = async function () {
  const tabla = document.getElementById("tablaAntecedente");
  if (!tabla) return;

  tabla.innerHTML = `<tr><td colspan="7">Cargando...</td></tr>`;

  try {
    const resp = await fetch(API_ANTEC);

    if (!resp.ok) {
      const txt = await resp.text().catch(() => "");
      tabla.innerHTML = `<tr><td colspan="7">Error API (${resp.status}): ${txt || "sin detalle"}</td></tr>`;
      return;
    }

    const lista = await resp.json().catch(() => null);

    if (!Array.isArray(lista) || lista.length === 0) {
      tabla.innerHTML = `<tr><td colspan="7">Sin registros</td></tr>`;
      return;
    }

    tabla.innerHTML = "";
    lista.forEach(a => {
      const fechaStr = (a?.fechaDiag || "").toString().slice(0, 10);
      tabla.innerHTML += `
        <tr>
          <td>${a?.idAntec ?? ""}</td>
          <td>${a?.codPart ?? ""}</td>
          <td>${a?.diagnostico ?? ""}</td>
          <td>${fechaStr}</td>
          <td>${a?.famCg ?? ""}</td>
          <td>${a?.famOtro ?? ""}</td>
          <td>${a?.cirugia ?? ""}</td>
        </tr>
      `;
    });
  } catch (e) {
    console.error("Antecedente listar error:", e);
    tabla.innerHTML = `<tr><td colspan="7">No se pudo conectar a ${API_ANTEC}</td></tr>`;
  }
};

async function buscarAntecedentePorCodPart(codPart) {
  try {
    const resp = await fetch(`${API_ANTEC}/por-participante/${encodeURIComponent(codPart)}`);
    if (!resp.ok) return null;
    return await resp.json();
  } catch {
    return null;
  }
}

let guardandoAntec = false;

async function guardarAntecedente() {
  const codPart = (document.getElementById("codPartAntec")?.value || "").trim();
  if (!codPart) {
    msgAntec("Primero guarda el participante (código vacío).", "err");
    return;
  }

  const caso = esCasoActual();

  const diagnostico = caso ? leerRadio("diagnosticoAntec") : null;
  const fechaDiagRaw = caso ? (document.getElementById("fechaDiagAntec")?.value || "").trim() : "";
  const fechaDiag = caso ? (fechaDiagRaw || null) : null;

  const famCg = leerRadio("famCgAntec");
  const famOtro = leerRadio("famOtroAntec");
  const cirugia = leerRadio("cirugiaAntec");

  const otroCancer =
    famOtro === "Sí" ? ((document.getElementById("otroCancerAntec")?.value || "").trim() || null) : null;

  const otrasEnfermedades = (document.getElementById("otrasEnfAntec")?.value || "").trim() || null;
  const medicamentos = (document.getElementById("medicamentosAntec")?.value || "").trim() || null;

  // Validaciones
  if (!famCg || !famOtro || !cirugia) {
    msgAntec("Completa: Fam. cáncer gástrico, Fam. otros cánceres y Cirugía previa.", "err");
    return;
  }
  if (famOtro === "Sí" && !otroCancer) {
    msgAntec("Si Fam. otros cánceres = Sí, debes indicar cuál(es).", "err");
    return;
  }
  if (caso && !diagnostico) {
    msgAntec("En CASO debes marcar diagnóstico histológico (Sí/No).", "err");
    return;
  }
  if (caso && diagnostico === "Sí" && !fechaDiag) {
    msgAntec("Si diagnóstico = Sí, debes ingresar fecha de diagnóstico.", "err");
    return;
  }

  if (guardandoAntec) return;
  guardandoAntec = true;

  try {
    const existente = await buscarAntecedentePorCodPart(codPart);

    const payload = {
      idAntec: existente?.idAntec ?? null,
      codPart,
      diagnostico,
      fechaDiag,
      famCg,
      famOtro,
      otroCancer,
      otrasEnfermedades,
      medicamentos,
      cirugia
    };

    let url = API_ANTEC;
    let method = "POST";

    if (existente?.idAntec != null) {
      url = `${API_ANTEC}/${encodeURIComponent(existente.idAntec)}`;
      method = "PUT";
    }

    const resp = await fetch(url, {
      method,
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });

    if (!resp.ok) {
      const txt = await resp.text().catch(() => "");
      msgAntec(txt || `Error al guardar (${resp.status})`, "err");
      return;
    }

    await resp.json().catch(() => null);

    msgAntec(method === "POST" ? "Antecedente guardado ✅" : "Antecedente actualizado ✅", "ok");
    await window.listarAntecedente();
  } catch (e) {
    console.error("Antecedente guardar error:", e);
    msgAntec("No se pudo conectar al backend de Antecedente.", "err");
  } finally {
    guardandoAntec = false;
  }
}

// ---------- Init ----------
document.addEventListener("DOMContentLoaded", async () => {
  const tabla = document.getElementById("tablaAntecedente");
  if (tabla) {
    // Si esto NO aparece, tu archivo no está cargando (ruta/nombre)
    tabla.innerHTML = `<tr><td colspan="7">JS antecedente cargado, consultando API...</td></tr>`;
  }

  const btnGuardar = document.getElementById("btnGuardarAntec");
  const btnLimpiar = document.getElementById("btnLimpiarAntec");

  if (btnGuardar) btnGuardar.addEventListener("click", guardarAntecedente);
  if (btnLimpiar) btnLimpiar.addEventListener("click", limpiarAntecedenteForm);

  document.querySelectorAll("input[name='famOtroAntec']").forEach(r => {
    r.addEventListener("change", window.actualizarVisibilidadAntecedente);
  });

  document.querySelectorAll("input[name='grupo']").forEach(r => {
    r.addEventListener("change", window.actualizarVisibilidadAntecedente);
  });

  window.actualizarVisibilidadAntecedente();
  await window.listarAntecedente();
});
