package net.passioncloud.upload;


import net.passioncloud.upload.storage.StorageFileNotFoundException;
import net.passioncloud.upload.storage.StorageService;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.file.Paths;
import java.util.stream.Stream;


@AutoConfigureMockMvc
@SpringBootTest
public class FileUploadTests {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private StorageService storageService;

    @Test
    public void shouldListAllFiles() throws Exception {
        BDDMockito.given(this.storageService.loadAll()).willReturn(
                Stream.of(Paths.get("first.txt"), Paths.get("second.txt"), Paths.get("third.txt"))
        );
        this.mvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.model().attribute("files",
                        Matchers.contains(
                                "http://localhost/files/first.txt",
                                "http://localhost/files/second.txt",
                                "http://localhost/files/third.txt")));
    }

    @Test
    public void shouldSaveUploadedFile() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "text.txt", "text/plain", "Hallelujah".getBytes());
        this.mvc.perform(MockMvcRequestBuilders
                .multipart("/")
                .file(multipartFile)
        ).andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.header().string("location", "/"));
        BDDMockito.then(this.storageService).should().store(multipartFile);
    }

    @Test
    public void shouldSayNotFoundWhenFileIsMissing() throws Exception {
        BDDMockito.given(storageService.loadAsResource("a.txt")).willThrow(StorageFileNotFoundException.class);
        this.mvc.perform(MockMvcRequestBuilders.get("/files/a.txt"))
                .andExpect(MockMvcResultMatchers.status().is(404));
    }

}
