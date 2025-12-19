/* Script para la página de login (index.html) */

const USER_API = "http://pacheco.chillan.ubiobio.cl:8034/api/usuario";

// Manejo de login
document.addEventListener('DOMContentLoaded', () => {
  const loginForm = document.getElementById('loginForm');

  if (loginForm) {
    loginForm.addEventListener('submit', async (e) => {
      e.preventDefault();
      const userIdRaw = document.getElementById('userId')?.value;
      const passwordInput = document.getElementById('password')?.value;
      const user = userIdRaw ? userIdRaw.toString().trim() : '';
      const password = passwordInput ? passwordInput.trim() : '';
      const box = document.getElementById('msgBox');

      if (!user) {
        if (box) {
          box.className = 'msg show err';
          box.textContent = 'Ingresa el ID de usuario.';
        }
        return;
      }

      if (!password) {
        if (box) {
          box.className = 'msg show err';
          box.textContent = 'Ingresa la contraseña.';
        }
        return;
      }

      // Obtener el usuario desde la API
      try {
        const resp = await fetch(`${USER_API}/${parseInt(user, 10)}`);

        if (!resp.ok) {
          if (box) {
            box.className = 'msg show err';
            box.textContent = 'Usuario no encontrado.';
          }
          return;
        }

        const usuario = await resp.json();

        // Validación rudimentaria de password
        if (usuario.password !== password) {
          if (box) {
            box.className = 'msg show err';
            box.textContent = 'Contraseña incorrecta.';
          }
          return;
        }

        // Login exitoso
        // Guardar userId y el nombre real del usuario en sessionStorage
        sessionStorage.setItem('userId', user);
        sessionStorage.setItem('userName', usuario.nombre || `Usuario ${user}`);
        sessionStorage.setItem('userRol', usuario.rol || 'Usuario');

        if (box) {
          box.className = 'msg show ok';
          box.textContent = 'Ingreso correcto. Redirigiendo...';
        }
        setTimeout(() => {
          window.location.href = 'home.html';
        }, 500);

      } catch (error) {
        console.error('Error al obtener usuario:', error);
        if (box) {
          box.className = 'msg show err';
          box.textContent = 'Error al conectar con el servidor.';
        }
      }
    });

    const backLogin = document.getElementById('btnBackLogin');
    if (backLogin) {
      backLogin.addEventListener('click', () => {
        window.location.href = 'home.html';
      });
    }
  }
});

