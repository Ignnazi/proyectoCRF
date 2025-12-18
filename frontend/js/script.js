
const API = "http://localhost:8080/api/participantecrf";
const USER_API = "http://localhost:8080/api/usuario";
const SOCIODEMO_API = "http://localhost:8080/api/sociodemo";

/* ================= UTILIDADES ================= */

window.showMsg = function(texto, tipo = "ok", elementId = "msgBox") {
  const box = document.getElementById(elementId);
  if (!box) return;
  
  box.style.display = 'block';
  box.className = "msg show " + (tipo === "err" ? "err" : "ok");
  box.textContent = texto;
  
  setTimeout(() => {
    box.style.display = 'none';
    box.classList.remove("show");
  }, 4000);
}

window.aLocalDateTime = function(fechaYYYYMMDD) {
  if (!fechaYYYYMMDD) return "";
  return `${fechaYYYYMMDD}T00:00:00`;
}

/* ================= LÓGICA DE BÚSQUEDA (busqueda.html) ================= */

window.buscarParticipante = async function() {
    const codigoInput = document.getElementById("searchCodPart");
    const resultDiv = document.getElementById("searchResult");
    const msgDivId = "searchMsg"; 

    if (!codigoInput || !resultDiv) return;

    const codigo = codigoInput.value.trim();
    if (!codigo) {
        showMsg("Por favor, ingrese un código.", "err", msgDivId);
        return;
    }

    // Mostrar estado de carga
    resultDiv.classList.remove("hidden");
    resultDiv.innerHTML = "<p>Buscando...</p>";

    try {
        const resp = await fetch(`${API}/${encodeURIComponent(codigo)}`);

        if (resp.status === 404) {
            resultDiv.innerHTML = "";
            showMsg("Participante no encontrado.", "err", msgDivId);
            return;
        }

        if (!resp.ok) {
            resultDiv.innerHTML = "";
            showMsg("Error en la búsqueda.", "err", msgDivId);
            return;
        }

        const p = await resp.json();
        const fechaStr = p.fechaInclusion ? p.fechaInclusion.toString().slice(0, 10) : "Sin fecha";
        
        resultDiv.innerHTML = `
            <div class="result-card">
                <h3>Resultados para: ${p.codPart}</h3>
                <div class="result-item"><span class="result-label">Nombre:</span> ${p.nombre}</div>
                <div class="result-item"><span class="result-label">Grupo:</span> ${p.grupo}</div>
                <div class="result-item"><span class="result-label">Fecha Inclusión:</span> ${fechaStr}</div>
                <div class="result-item"><span class="result-label">Registrado por ID:</span> ${p.idUser || "N/A"}</div>
            </div>
        `;

    } catch (e) {
        console.error(e);
        resultDiv.innerHTML = "";
        showMsg("Error de conexión con el servidor.", "err", msgDivId);
    }
}

/* ================= LÓGICA DE FORMULARIO (formulario.html) ================= */
// (Se mantiene igual para soportar el guardado)

window.guardarParticipante = async function() {
  const nombreEl = document.getElementById("nombre");
  const fechaEl = document.getElementById("fecha");
  const grupoEl = document.querySelector("input[name='grupo']:checked");

  if (!nombreEl || !fechaEl || !grupoEl) {
    showMsg("Complete todos los campos.", "err", "msgBox");
    return;
  }

  const body = {
    nombre: nombreEl.value.trim(),
    grupo: grupoEl.value,
    fechaInclusion: aLocalDateTime(fechaEl.value)
  };

  try {
    const resp = await fetch(API, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body)
    });

    if (!resp.ok) {
      const txt = await resp.text();
      showMsg("Error: " + txt, "err", "msgBox");
      return;
    }

    const creado = await resp.json();
    showMsg(`Guardado exitoso. Código: ${creado.codPart}`, "ok", "msgBox");
    
    // Guardar código para la siguiente sección
    const hiddenInput = document.getElementById("currentCodPart");
    if(hiddenInput) hiddenInput.value = creado.codPart;

    // Limpiar campos y refrescar tabla
    nombreEl.value = "";
    fechaEl.value = "";
    grupoEl.checked = false;
    
    if(typeof listarParticipantes === 'function') listarParticipantes();

  } catch (e) {
    console.error(e);
    showMsg("Error de conexión.", "err", "msgBox");
  }
}

// Función auxiliar para listar en la tabla de formulario.html
window.listarParticipantes = async function() {
    const tabla = document.getElementById("tablaParticipantes");
    if (!tabla) return;
    
    try {
        const resp = await fetch(API);
        if(!resp.ok) return;
        const lista = await resp.json();
        
        tabla.innerHTML = "";
        if (lista.length === 0) {
             tabla.innerHTML = `<tr><td colspan="4">Sin registros</td></tr>`;
             return;
        }
        
        lista.forEach(p => {
            const fechaStr = (p.fechaInclusion || "").toString().slice(0, 10);
            tabla.innerHTML += `
                <tr>
                    <td>${p.codPart}</td>
                    <td>${p.nombre}</td>
                    <td>${p.grupo}</td>
                    <td>${fechaStr}</td>
                </tr>
            `;
        });
    } catch(e) { console.error(e); }
}


/* ================= INICIALIZACIÓN DE EVENTOS ================= */

document.addEventListener('DOMContentLoaded', () => {
    
    // --- Detectar en qué página estamos ---

    // 1. HOME.HTML
    const btnInfo = document.getElementById('btnInfo');
    if (btnInfo) {
        // Redirigir a busqueda.html
        btnInfo.addEventListener('click', () => { window.location.href = 'busqueda.html'; });
    }

    const btnForm = document.getElementById('btnForm');
    if (btnForm) {
        // Redirigir a formulario.html
        btnForm.addEventListener('click', () => { window.location.href = 'formulario.html'; });
    }

    const btnLogin = document.getElementById('btnLogin');
    if (btnLogin) {
        // Redirigir a login (o index.html si lo usas para login)
        btnLogin.addEventListener('click', () => { window.location.href = 'index.html'; });
    }
    const btnExport = document.getElementById('btnExport');
    if (btnExport) {
    btnExport.addEventListener('click', () => { window.location.href = 'exportar.html'; });
    }

    // 2. BUSQUEDA.HTML
    const btnSearchAction = document.getElementById('btnSearchAction');
    if (btnSearchAction) {
        btnSearchAction.addEventListener('click', window.buscarParticipante);
        
        // Permitir buscar con Enter
        const inputSearch = document.getElementById('searchCodPart');
        if(inputSearch) {
            inputSearch.addEventListener('keypress', function (e) {
                if (e.key === 'Enter') window.buscarParticipante();
            });
        }
    }

    const btnBackHome = document.getElementById('btnBackHome');
    if (btnBackHome) {
        btnBackHome.addEventListener('click', () => { window.location.href = 'home.html'; });
    }

    // 3. FORMULARIO.HTML
    const btnGuardar = document.getElementById('btnGuardar');
    if (btnGuardar) {
        btnGuardar.addEventListener('click', window.guardarParticipante);
        // Cargar la tabla al inicio
        window.listarParticipantes();
    }
    
    const btnBackForm = document.getElementById('btnBackForm');
    if(btnBackForm) {
        btnBackForm.addEventListener('click', () => { window.location.href = 'home.html'; });
    }
});