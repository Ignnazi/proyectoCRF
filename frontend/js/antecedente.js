<<<<<<< Updated upstream
const API_ANTEC = "http://localhost:8082/api/antecedente";
=======
const API_ANTEC = "http://pacheco.chillan.ubiobio.cl:8034/api/antecedente";
>>>>>>> Stashed changes

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

function getCodPartActual() {
  return (
    document.getElementById("codPart")?.value ||
    window.estadoFormulario?.codActual ||
    window.estadoFormulario?.cod_part ||
    localStorage.getItem("codPart") ||
    localStorage.getItem("cod_part") ||
    ""
  ).trim();
}

function syncCodPartAntec() {
  const v = getCodPartActual();
  const input = document.getElementById("codPartAntec");
  if (input && v && input.value !== v) input.value = v;
}

function esCasoActual() {
  const grupo = document.querySelector("input[name='grupo']:checked")?.value || "";
  return String(grupo).toLowerCase() === "caso";
}

window.actualizarVisibilidadAntecedente = function () {
  const solo = document.getElementById("soloCasosBlock");
  if (solo) {
    const caso = esCasoActual();
    solo.style.display = caso ? "block" : "none";
    if (!caso) {
      document.querySelectorAll("input[name='diagnosticoAntec']").forEach(r => (r.checked = false));
      const fecha = document.getElementById("fechaDiagAntec");
      if (fecha) fecha.value = "";
    }
  }

  const diag = leerRadio("diagnosticoAntec");
  const fechaBlock = document.getElementById("fechaDiagBlock");
  if (fechaBlock) fechaBlock.style.display = diag === "Sí" ? "block" : "none";
  if (diag !== "Sí") {
    const fecha = document.getElementById("fechaDiagAntec");
    if (fecha) fecha.value = "";
  }

  const famOtro = leerRadio("famOtroAntec");
  const cualBlock = document.getElementById("cualCancerBlock");
  if (cualBlock) cualBlock.style.display = famOtro === "Sí" ? "block" : "none";
  if (famOtro !== "Sí") {
    const otro = document.getElementById("otroCancerAntec");
    if (otro) otro.value = "";
  }

  const medGastro = leerRadio("medGastroAntec");
  const medBlock = document.getElementById("medGastroCualBlock");
  if (medBlock) medBlock.style.display = medGastro === "Sí" ? "block" : "none";
  if (medGastro !== "Sí") {
    const cual = document.getElementById("medGastroCualAntec");
    if (cual) cual.value = "";
  }
};

function limpiarAntecedenteForm() {
  document.querySelectorAll("input[name='diagnosticoAntec']").forEach(r => (r.checked = false));
  document.querySelectorAll("input[name='famCgAntec']").forEach(r => (r.checked = false));
  document.querySelectorAll("input[name='famOtroAntec']").forEach(r => (r.checked = false));
  document.querySelectorAll("input[name='cirugiaAntec']").forEach(r => (r.checked = false));
  document.querySelectorAll("input[name='medGastroAntec']").forEach(r => (r.checked = false));

  const fecha = document.getElementById("fechaDiagAntec");
  if (fecha) fecha.value = "";

  ["otroCancerAntec", "otrasEnfAntec", "medGastroCualAntec"].forEach(id => {
    const el = document.getElementById(id);
    if (el) el.value = "";
  });

  window.actualizarVisibilidadAntecedente();
}

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
    console.error(e);
    tabla.innerHTML = `<tr><td colspan="7">No se pudo conectar a ${API_ANTEC}</td></tr>`;
  }
};

async function buscarAntecedentePorCodPart(codPart) {
  try {
    const resp2 = await fetch(API_ANTEC);
    if (!resp2.ok) return null;
    const lista = await resp2.json();
    return Array.isArray(lista) ? (lista.find(x => x?.codPart === codPart) || null) : null;
  } catch {
    return null;
  }
}

let guardandoAntec = false;

async function guardarAntecedente() {
  syncCodPartAntec();
  const codPart = getCodPartActual();
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

  const medGastro = leerRadio("medGastroAntec");
  const medGastroCual =
    medGastro === "Sí" ? ((document.getElementById("medGastroCualAntec")?.value || "").trim() || null) : null;

  if (!famCg || !famOtro || !cirugia || !medGastro) {
    msgAntec("Completa: Fam. cáncer gástrico, Fam. otros cánceres, Medicamentos (Sí/No) y Cirugía previa.", "err");
    return;
  }
  if (famOtro === "Sí" && !otroCancer) {
    msgAntec("Si Fam. otros cánceres = Sí, debes indicar cuál(es).", "err");
    return;
  }
  if (medGastro === "Sí" && !medGastroCual) {
    msgAntec("Si medicamentos gastrolesivos = Sí, debes especificar cuál.", "err");
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
      codPart,
      diagnostico,
      fechaDiag,
      famCg,
      famOtro,
      otroCancer,
      otrasEnfermedades,
      medGastro,
      medGastroCual,
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

    msgAntec(method === "POST" ? "Antecedente guardado ✅" : "Antecedente actualizado ✅", "ok");
    await window.listarAntecedente();
  } catch (e) {
    console.error(e);
    msgAntec("No se pudo conectar al backend de Antecedente.", "err");
  } finally {
    guardandoAntec = false;
  }
}

document.addEventListener("DOMContentLoaded", async () => {
  syncCodPartAntec();

  document.getElementById("codPart")?.addEventListener("input", syncCodPartAntec);
  document.getElementById("codPart")?.addEventListener("change", syncCodPartAntec);
  document.getElementById("sec3")?.addEventListener("click", syncCodPartAntec);

  document.getElementById("btnGuardarAntec")?.addEventListener("click", guardarAntecedente);
  document.getElementById("btnLimpiarAntec")?.addEventListener("click", limpiarAntecedenteForm);

  document.querySelectorAll("input[name='famOtroAntec']").forEach(r => r.addEventListener("change", window.actualizarVisibilidadAntecedente));
  document.querySelectorAll("input[name='medGastroAntec']").forEach(r => r.addEventListener("change", window.actualizarVisibilidadAntecedente));
  document.querySelectorAll("input[name='diagnosticoAntec']").forEach(r => r.addEventListener("change", window.actualizarVisibilidadAntecedente));
  document.querySelectorAll("input[name='grupo']").forEach(r => r.addEventListener("change", window.actualizarVisibilidadAntecedente));

  window.actualizarVisibilidadAntecedente();
  await window.listarAntecedente();
});
