package br.com.cueva.webservice.controller;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.cueva.webservice.util.ManualAuthorization;
import br.com.housecash.backend.App;
import br.com.housecash.backend.model.Flatmate;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = App.class)
@Sql("classpath:reset.sql")
public class FlatmateControllerTest {

	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private FilterChainProxy springSecurityFilterChain;

	private MockMvc mockMvc;

    private static final ObjectMapper om = new ObjectMapper();

//    @MockBean
//    private FlatmateRepository mockRepository;

    @Before
    public void init() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).addFilter(springSecurityFilterChain).build();
		
//        Flatmate flatmate = new Flatmate("1212test@mail.com", "test");
//        flatmate.setNickname("Flat 1");
//        flatmate.setId(1L);
//        when(mockRepository.findById(1L)).thenReturn(Optional.of(flatmate));

    }

    @Test
    public void find_flatmateId_OK() throws Exception {
    	
    	ManualAuthorization jean = ManualAuthorization.setupWithToken(mockMvc, "jean@mail.com", "test");
    	
        mockMvc.perform(jean.get("/flatmates/9").response())
                /*.andDo(print())*/
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(9)))
                .andExpect(jsonPath("$.email", is("gretchen@mail.com")))
                .andExpect(jsonPath("$.nickname", is("Gretchen (test)")))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.firstStep", is(false)))
                .andExpect(jsonPath("$.guestStep", is(false)));

//        verify(mockRepository, times(1)).findById(9L);

    }

    @Test
    public void find_allFlatmates_OK() throws Exception {
    	
    	ManualAuthorization jean = ManualAuthorization.setupWithToken(mockMvc, "jean@mail.com", "test");

//        List<Flatmate> flatmates = Arrays.asList(
//                new Flatmate("Flatmate A", ""),
//                new Flatmate("Flatmate B", ""));
//
//        when(mockRepository.findAll()).thenReturn(flatmates);

        mockMvc.perform(jean.get("/flatmates").response())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(9)))
                .andExpect(jsonPath("$[0].email", is("gretchen@mail.com")))
                .andExpect(jsonPath("$[0].nickname", is("Gretchen (test)")))
                .andExpect(jsonPath("$[0].password").doesNotExist())
                .andExpect(jsonPath("$[1].id", is(10)))
                .andExpect(jsonPath("$[1].email", is("fernando@mail.com")))
                .andExpect(jsonPath("$[1].nickname", is("Fernando (test)")))
                .andExpect(jsonPath("$[1].password").doesNotExist())
                .andExpect(jsonPath("$[2].id", is(1)))
                .andExpect(jsonPath("$[2].email", is("marcelo@mail.com")))
                .andExpect(jsonPath("$[2].nickname", is("Marcelo (test)")))
                .andExpect(jsonPath("$[2].password").doesNotExist());

//        verify(mockRepository, times(1)).findAll();
    }

    @Test
    public void flatmateIdNotFound_404() throws Exception {
    	
    	ManualAuthorization jean = ManualAuthorization.setupWithToken(mockMvc, "jean@mail.com", "test");
        mockMvc.perform(jean.get("/flatmates/5").response()).andExpect(status().isNotFound());
        
    }

    @Test
    public void save_flatmate_OK() throws Exception {
    	
    	ManualAuthorization jean = ManualAuthorization.setupWithToken(mockMvc, "jean@mail.com", "test");
    	
    	String email = "newFlatmate@mail.com";
    	String nickname = "New Flatmate";
    	String password = "test";

        String newFlatmate = "{\"email\":\""+email+"\",\"nickname\":\""+nickname+"\",\"password\":\""+password+"\"}";
    	
//        Flatmate newFlatmate = new Flatmate("newFlatmate@mail.com", "New Flatmate", "test");
//        System.out.println("om.writeValueAsString(newFlatmate): " + om.writeValueAsString(newFlatmate));
//      when(mockRepository.save(any(Flatmate.class))).thenReturn(newFlatmate);

        mockMvc.perform(jean.post("/flatmates").response()
                .content(newFlatmate)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
        		/*.andDo(print())*/
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(11)))
                .andExpect(jsonPath("$.email", is(email)))
                .andExpect(jsonPath("$.nickname", is(nickname)))
                .andExpect(jsonPath("$.password").doesNotExist());

//        verify(mockRepository, times(1)).save(any(Flatmate.class));

    }

    @Test
    public void update_flatmate_Forbidden_Role_USER() throws Exception {
    	
    	ManualAuthorization jean = ManualAuthorization.setupWithToken(mockMvc, "jean@mail.com", "test");

        Flatmate updateFlatmate = new Flatmate("update@mail.com", "Flatmate updated", "test");
//        when(mockRepository.save(any(Flatmate.class))).thenReturn(updateFlatmate);

        mockMvc.perform(jean.put("/flatmates/8").response()
                .content(om.writeValueAsString(updateFlatmate))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isForbidden());

    }

    @Test
    public void update_flatmate_OK() throws Exception {
    	
    	ManualAuthorization marcelo = ManualAuthorization.setupWithToken(mockMvc, "marcelo@mail.com", "test");
    	
    	String email = "carol2@mail.com";
    	String nickname = "Carol (test UPDATE)";
    	String password = "test";
    	String roles = "USER";
    	String enabled = "true";
    	String firstStep = "true";
    	String guestStep = "true";

		String updateFlatmate = "{\"email\":\"" + email + "\",\"nickname\":\"" + nickname + "\",\"password\":\""
				+ password + "\"," + "\"roles\":\"" + roles + "\"," + "\"enabled\":\"" + enabled + "\","
				+ "\"firstStep\":\"" + firstStep + "\"," + "\"guestStep\":\"" + guestStep + "\"}";

//        Flatmate updateFlatmate = new Flatmate("carol2@mail.com", "Carol (test UPDATE)", "test");
//        when(mockRepository.save(any(Flatmate.class))).thenReturn(updateFlatmate);

        mockMvc.perform(marcelo.put("/flatmates/6").response()
                .content(updateFlatmate)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(6)))
                .andExpect(jsonPath("$.email", is("carol2@mail.com")))
                .andExpect(jsonPath("$.nickname", is("Carol (test UPDATE)")))
                .andExpect(jsonPath("$.password").doesNotExist());

    }

    @Test
    public void patch_flatmateNickname_OK() throws Exception {
    	
    	ManualAuthorization jean = ManualAuthorization.setupWithToken(mockMvc, "jean@mail.com", "test");

//        when(mockRepository.save(any(Flatmate.class))).thenReturn(new Flatmate());
        String patchInJson = "{\"nickname\":\"Jean (test UPDATE)\"}";

        mockMvc.perform(jean.patch("/flatmates/8").response()
                .content(patchInJson)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(8)))
                .andExpect(jsonPath("$.email", is("jean@mail.com")))
                .andExpect(jsonPath("$.nickname", is("Jean (test UPDATE)")))
                .andExpect(jsonPath("$.password").doesNotExist());

//        verify(mockRepository, times(1)).findById(1L);
//        verify(mockRepository, times(1)).save(any(Flatmate.class));

    }

//    @Test
//    public void patch_flatmateNickname_Fail() throws Exception {
//    	
//    	ManualAuthorization jean = ManualAuthorization.setupWithToken(mockMvc, "jean@mail.com", "test");
//
//        String patchInJson = "{\"nickname\":\"Gretchen (test UPDATE)\"}";
//
//        mockMvc.perform(jean.patch("/flatmates/9").response()
//                .content(patchInJson)
//                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
//                .andExpect(status().isForbidden());
//
//    }

//    @Test
//    public void patch_flatmateEmail_405() throws Exception {
//    	
//    	ManualAuthorization jean = ManualAuthorization.setupWithToken(mockMvc, "jean@mail.com", "test");
//
//        String patchInJson = "{\"email\":\"test@othermail.com\"}";
//
//        mockMvc.perform(jean.patch("/flatmates/8").response()
//                .content(patchInJson)
//                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
//                .andExpect(status().isMethodNotAllowed());
//
////        verify(mockRepository, times(1)).findById(1L);
////        verify(mockRepository, times(0)).save(any(Flatmate.class));
//        
//    }

//    @Test
//    public void delete_flatmate_Forbidden_Role_USER() throws Exception {
//    	
//    	Authorization jean = Authorization.setupWithToken(mockMvc, "jean@mail.com", "test");
//
////        doNothing().when(mockRepository).deleteById(1L);
//
//        mockMvc.perform(jean.delete("/flatmates/1").response())
//                /*.andDo(print())*/
//                .andExpect(status().isForbidden());
//
////        verify(mockRepository, times(1)).deleteById(1L);
//    }

//    @Test
//    public void delete_flatmate_OK() throws Exception {
//    	
//    	Authorization marcelo = Authorization.setupWithToken(mockMvc, "marcelo@mail.com", "test");
//
////        doNothing().when(mockRepository).deleteById(1L);
//
//        mockMvc.perform(marcelo.delete("/flatmates/1").response())
//                /*.andDo(print())*/
//                .andExpect(status().isOk());
//
////        verify(mockRepository, times(1)).deleteById(1L);
//    }

    private static void printJSON(Object object) {
        String result;
        try {
            result = om.writerWithDefaultPrettyPrinter().writeValueAsString(object);
            System.out.println(result);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
