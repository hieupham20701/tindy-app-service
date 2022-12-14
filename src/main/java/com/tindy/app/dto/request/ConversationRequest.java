package com.tindy.app.dto.request;

import com.tindy.app.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ConversationRequest {

    private String title;
    private User user;
    private List<String> phones;
    private Date createdAt;
    private Date updatedAt;
    private String status;
    private String avatar;
}
