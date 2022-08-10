package com.project.medtech.service;

import com.project.medtech.dto.TextDto;
import com.project.medtech.dto.UpdateContactDto;
import com.project.medtech.exception.AlreadyExistingException;
import com.project.medtech.exception.ResourceNotFoundException;
import com.project.medtech.model.ContactEntity;
import com.project.medtech.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;


    public List<String> getContacts() {
        return contactRepository.findAll().stream()
                .map(ContactEntity::getText)
                .collect(Collectors.toList());
    }

    public String createContact(TextDto textDto) {
        boolean existed = contactRepository.findByText(textDto.getText()).isPresent();

        if(existed) {
            throw new AlreadyExistingException("Contact is already existing.");
        }

        ContactEntity contact = new ContactEntity();

        contact.setText(textDto.getText());

        contactRepository.save(contact);

        return "New contact saved: " + contact.getText();
    }

    public String updateContact(UpdateContactDto updateContactDto) {
        ContactEntity contact = contactRepository.findByText(updateContactDto.getOldContact())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Contact was not found with text: " + updateContactDto.getOldContact())
                );

        contact.setText(updateContactDto.getNewContact());

        contactRepository.save(contact);

        return "Contact was updated with text: " + contact.getText();
    }

    public String deleteContact(TextDto textDto) {
        ContactEntity contact = contactRepository.findByText(textDto.getText())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Contact was not found with text: " + textDto.getText())
                );

        contactRepository.deleteById(contact.getId());

        return "Deleted contact with text: " + textDto.getText();
    }

}
