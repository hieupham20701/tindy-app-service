package com.tindy.app.controller;

import com.tindy.app.dto.request.AttachmentRequest;
import com.tindy.app.dto.request.MessageRequest;
import com.tindy.app.dto.respone.MessageResponse;
import com.tindy.app.model.entity.Attachments;
import com.tindy.app.service.AttachmentService;
import com.tindy.app.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@CrossOrigin
public class MessageController {

    private final MessageService messageService;
    private final AttachmentService attachmentService;
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<MessageResponse> saveMessage(@RequestParam String conversationId,@RequestParam String senderId,
                                                       @RequestParam String messageType, @RequestParam String message, @RequestParam( value = "file", required = false) List<MultipartFile> file) throws IOException {

        if(file != null){
            return ResponseEntity.ok().body(messageService.saveMessage(conversationId,senderId,messageType, message,file));
        }else{
            return ResponseEntity.ok().body(messageService.saveMessage(conversationId,senderId,messageType, message,null));

        }
    };
    @GetMapping("/{conversationId}")
    public ResponseEntity<List<MessageResponse>> getMessages(@PathVariable String conversationId){
        List<MessageResponse> messageResponses = messageService.getMessages(Integer.parseInt(conversationId));
        return ResponseEntity.ok().body(messageResponses);
    }

    @PostMapping("/attachments")
    public ResponseEntity<?> saveAttachment(@RequestParam("file")MultipartFile file, @RequestParam Integer messageId ){
        return ResponseEntity.ok().body(attachmentService.saveAttachment(messageId,file));
    }


    @PostMapping("/attachments/{messageId}")
    public ResponseEntity<?> downloadAttachment(@PathVariable String messageId, @RequestParam String fileName) throws IOException {
        return ResponseEntity.ok().body(attachmentService.downloadAttachment(Integer.parseInt(messageId),fileName));
    }

    @PostMapping("/recall/{id}")
    public ResponseEntity<?> deleteMessage(@PathVariable Integer id){
        return ResponseEntity.ok().body(messageService.deleteMessage(id));
    }
}
