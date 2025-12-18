const API_SOCIO = "http://localhost:8082/api/sociodemo";

function msgSocio(texto, tipo = "ok") {
  const box = document.getElementById("msgBoxSocio");
  if (!box) return;
  box.className = "msg show " + (tipo === "err" ? "err" : "ok");
  box.textContent = texto;
  setTimeout(() => box.classList.remove("show"), 3500);
}

function leerRadio(name) {
  const el = document.querySelector(`input[name='${name}']:checked`);
  return el ? el.value : "";
}

function limpiarSociodemoForm() {
  ["edadSocio","nacionalidadSocio","direccionSocio","ocupacionSocio"].forEach(id => {
    const el = document.getElementById(id);
    if (el) el.value = "";
  });
  ["sexoSocio","zonaSocio","aniosResSocio","educacionSocio"].forEach(n => {
    document.querySelectorAll(`input[name='${n}']`).forEach(r => r.checked = false);
  });
}

window.listarSociodemo = async function () {
  const tabla = document.getElementById("tablaSociodemo");
  if (!tabla) return;

  tabla.innerHTML = `<tr><td colspan="7">Cargando...</td></tr>`;

  try {
    const resp = await fetch(API_SOCIO);
    if (!resp.ok) {
      const txt = await resp.text();
      tabla.innerHTML = `<tr><td colspan="7">Error: ${txt}</td></tr>`;
      return;
    }

    const lista = await resp.json();
    if (!Array.isArray(lista) || lista.length === 0) {
      tabla.innerHTML = `<tr><td colspan="7">Sin registros</td></tr>`;
      return;
    }

    tabla.innerHTML = "";
    lista.forEach(s => {
      tabla.innerHTML += `
        <tr>
          <td>${s.idSocdemo ?? ""}</td>
          <td>${s.codPart ?? ""}</td>
          <td>${s.edad ?? ""}</td>
          <td>${s.sexo ?? ""}</td>
          <td>${s.zona ?? ""}</td>
          <td>${s.educacion ?? ""}</td>
          <td>${s.ocupacion ?? ""}</td>
        </tr>
      `;
    });
  } catch (e) {
    console.error(e);
    tabla.innerHTML = `<tr><td colspan="7">No se pudo cargar la lista</td></tr>`;
  }
};

async function buscarSociodemoPorCodPart(codPart) {
  try {
    const resp = await fetch(API_SOCIO);
    if (!resp.ok) return null;
    const lista = await resp.json();
    if (!Array.isArray(lista)) return null;
    return lista.find(x => (x?.codPart || "") === codPart) || null;
  } catch {
    return null;
  }
}

let guardandoSocio = false;

async function guardarSociodemo() {
  const codPart = (document.getElementById("codPartSocio")?.value || "").trim();
  if (!codPart) {
    msgSocio("Primero guarda el participante (código vacío).", "err");
    return;
  }

  const edadRaw = (document.getElementById("edadSocio")?.value || "").trim();
  const edad = edadRaw ? parseInt(edadRaw, 10) : null;

  const sexo = leerRadio("sexoSocio");
  const nacionalidad = (document.getElementById("nacionalidadSocio")?.value || "").trim();
  const direccion = (document.getElementById("direccionSocio")?.value || "").trim();
  const zona = leerRadio("zonaSocio");
  const aniosRes = leerRadio("aniosResSocio");
  const educacion = leerRadio("educacionSocio");
  const ocupacion = (document.getElementById("ocupacionSocio")?.value || "").trim();

  if (edad == null || Number.isNaN(edad)) { msgSocio("La edad es obligatoria.", "err"); return; }
  if (!sexo || !zona || !aniosRes || !educacion) {
    msgSocio("Completa: sexo, zona, años residencia y nivel educacional.", "err");
    return;
  }

  if (guardandoSocio) return;
  guardandoSocio = true;

  try {
    const existente = await buscarSociodemoPorCodPart(codPart);

    const payload = { edad, sexo, nacionalidad, direccion, zona, aniosRes, educacion, ocupacion, codPart };

    let url = API_SOCIO;
    let method = "POST";

    if (existente && existente.idSocdemo != null) {
      url = `${API_SOCIO}/${encodeURIComponent(existente.idSocdemo)}`;
      method = "PUT";
    }

    const resp = await fetch(url, {
      method,
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });

    if (!resp.ok) {
      const txt = await resp.text();
      msgSocio("Error al guardar: " + txt, "err");
      return;
    }

    const txt = await resp.text();
    msgSocio(txt || (method === "POST" ? "Guardado ✅" : "Actualizado ✅"), "ok");

    await window.listarSociodemo();
  } catch (e) {
    console.error(e);
    msgSocio("No se pudo conectar al backend de Sociodemo.", "err");
  } finally {
    guardandoSocio = false;
  }
}

document.addEventListener("DOMContentLoaded", async () => {
  const btnGuardarSocio = document.getElementById("btnGuardarSocio");
  if (btnGuardarSocio) btnGuardarSocio.addEventListener("click", () => guardarSociodemo());

  const btnLimpiarSocio = document.getElementById("btnLimpiarSocio");
  if (btnLimpiarSocio) btnLimpiarSocio.addEventListener("click", () => limpiarSociodemoForm());

  await window.listarSociodemo();
});
