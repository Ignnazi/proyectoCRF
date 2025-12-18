package ingsof;

import com.fasterxml.jackson.databind.ObjectMapper;
import ingsof.controlador.ParticipantecrfC;
import ingsof.controlador.UsuarioC;
import ingsof.entidad.Participantecrf;
import ingsof.entidad.Usuario;
import ingsof.servicio.ParticipantecrfS;
import ingsof.servicio.UsuarioS;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Probamos solo estos dos controladores para no cargar todo el contexto
@WebMvcTest({ParticipantecrfC.class, UsuarioC.class})
class ControladoresRestCT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ParticipantecrfS participanteS;

    @MockBean
    private UsuarioS usuarioS;

    @Autowired
    private ObjectMapper objectMapper; // Para convertir objetos a JSON

    // --- CASO 5: GET /api/participantecrf (Listar) ---
    @Test
    void testListarParticipantes_Retorna200YLista() throws Exception {
        Participantecrf p1 = new Participantecrf(); p1.setCodPart("CS001");
        Participantecrf p2 = new Participantecrf(); p2.setCodPart("CT001");

        given(participanteS.listar()).willReturn(Arrays.asList(p1, p2));

        mockMvc.perform(get("/api/participantecrf"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].codPart").value("CS001"));
    }

    // --- CASO 6: GET /api/participantecrf/{id} (Buscar uno) ---
    @Test
    void testBuscarParticipante_SiExiste_Retorna200() throws Exception {
        Participantecrf p = new Participantecrf();
        p.setCodPart("CS005");
        p.setNombre("Juan Perez");
        p.setGrupo("Caso");

        given(participanteS.buscar("CS005")).willReturn(Optional.of(p));

        mockMvc.perform(get("/api/participantecrf/CS005"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan Perez"));
    }

    // --- CASO 7: POST /api/participantecrf (Crear) ---
    @Test
    void testCrearParticipante_Retorna200() throws Exception {
        Participantecrf nuevo = new Participantecrf();
        nuevo.setNombre("Nuevo Test");
        nuevo.setGrupo("Control");

        Participantecrf creado = new Participantecrf();
        creado.setCodPart("CT099");
        creado.setNombre("Nuevo Test");
        creado.setGrupo("Control");

        given(participanteS.crear(any(Participantecrf.class))).willReturn(creado);

        mockMvc.perform(post("/api/participantecrf")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nuevo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codPart").value("CT099"));
    }

    // --- CASO 8: POST /api/usuario/login (Login Personalizado) ---
    @Test
    void testLogin_CredencialesCorrectas_Retorna200() throws Exception {
        UsuarioC.Credenciales creds = new UsuarioC.Credenciales();
        creds.id = 1;
        creds.password = "secret";

        Usuario u = new Usuario();
        u.setIdUser(1);
        u.setNombre("Admin");
        u.setPassword("secret");

        given(usuarioS.obtenerPorId(1)).willReturn(u);

        mockMvc.perform(post("/api/usuario/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(creds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Admin"));
    }
    
    // --- CASO 9: Login Fallido ---
    @Test
    void testLogin_PasswordIncorrecta_Retorna401() throws Exception {
        UsuarioC.Credenciales creds = new UsuarioC.Credenciales();
        creds.id = 1;
        creds.password = "incorrecta";

        Usuario u = new Usuario();
        u.setIdUser(1);
        u.setPassword("original");

        given(usuarioS.obtenerPorId(1)).willReturn(u);

        mockMvc.perform(post("/api/usuario/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(creds)))
                .andExpect(status().isUnauthorized());
    }
}