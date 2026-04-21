package com.nosmoke.nexus_ai.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nosmoke.nexus_ai.dtos.ErrorRequest;
import com.nosmoke.nexus_ai.dtos.ErrorResponse;
import com.nosmoke.nexus_ai.model.ErrorLog;
import com.nosmoke.nexus_ai.model.ErrorLog.Component;
import com.nosmoke.nexus_ai.model.ErrorLog.Environment;
import com.nosmoke.nexus_ai.model.ErrorLog.Level;
import com.nosmoke.nexus_ai.model.ErrorLog.Status;
import com.nosmoke.nexus_ai.service.ErrorService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/errors")
@RequiredArgsConstructor
public class ErrorController {

    private final ErrorService errorService;

    // create
    @PostMapping("/submit")
    public ResponseEntity<ErrorResponse> submitError(@Valid @RequestBody ErrorRequest errorRequest) {

        return ResponseEntity.status(HttpStatus.CREATED).body(errorService.create(errorRequest));

    }

    @GetMapping("/all")
    public ResponseEntity<List<ErrorResponse>> readAll() {
        return ResponseEntity.status(HttpStatus.OK).body(errorService.readAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ErrorResponse> getErrorById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(errorService.read(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteError(@PathVariable Long id) {
        errorService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/application_name")
    public ResponseEntity<List<ErrorResponse>> getByApplicationName(@RequestParam String applicationName) {
        return ResponseEntity.status(HttpStatus.OK).body(errorService.getByApplicationName(applicationName));
    }

    @GetMapping("/environment")
    public ResponseEntity<List<ErrorResponse>> getByEnvironment(@RequestParam Environment environment) {
        return ResponseEntity.status(HttpStatus.OK).body(errorService.getByEnvironment(environment));
    }

    @GetMapping("/status")
    public ResponseEntity<List<ErrorResponse>> getByStatus(@RequestParam Status status) {
        return ResponseEntity.status(HttpStatus.OK).body(errorService.getByStatus(status));
    }

    @GetMapping("/level")
    public ResponseEntity<List<ErrorResponse>> getByLevel(@RequestParam Level level) {
        return ResponseEntity.status(HttpStatus.OK).body(errorService.getByLevel(level));
    }

    @GetMapping("/component")
    public ResponseEntity<List<ErrorResponse>> getByComponent(@RequestParam Component component) {
        return ResponseEntity.status(HttpStatus.OK).body(errorService.getByComponent(component));
    }

}
