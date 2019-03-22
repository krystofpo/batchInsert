package cz.krystof.demolibrary.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAndBooksRequest {
    private UserRequest userRequest;
    private List<BookRequest> bookRequests = new ArrayList<>();
}
