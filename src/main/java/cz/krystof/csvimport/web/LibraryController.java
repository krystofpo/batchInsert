package cz.krystof.csvimport.web;


import cz.krystof.csvimport.ImportSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LibraryController {

    @Autowired
    ImportSystem importSystem;

    @GetMapping("/import")
    @ResponseBody
    public ResponseEntity csvImport() {
        return importSystem.csvImport();
    }

}
