package pt.ulisboa.tecnico.socialsoftware.tutor.statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.access.AccessService;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswersDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.ResultAnswersDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.SolvedQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementCreationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
@Secured({ "ROLE_ADMIN", "ROLE_STUDENT" })
@RequestMapping("/student/quizzes")
public class StatementController {
    private static Logger logger = LoggerFactory.getLogger(StatementController.class);

    @Autowired
    private StatementService statementService;

    @Autowired
    private AccessService accessService;

    @PostMapping("/generate")
    public StatementQuizDto getNewQuiz(Principal principal, @RequestBody StatementCreationDto quizDetails) {
        User user = (User) ((Authentication) principal).getPrincipal();

        accessService.create(user, LocalDateTime.now(), "GENERATE");

        return statementService.generateStudentQuiz(user, quizDetails.getNumberOfQuestions());
    }

    @GetMapping("/available")
    public List<StatementQuizDto> getAvailableQuizzes(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            return Collections.emptyList();
        }

        accessService.create(user, LocalDateTime.now(), "LIST_AVAILABLE");

        return statementService.getAvailableQuizzes(user);
    }

    @GetMapping("/solved")
    public List<SolvedQuizDto> getSolvedQuizzes(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            return Collections.emptyList();
        }

        accessService.create(user, LocalDateTime.now(), "LIST_SOLVED");

        return statementService.getSolvedQuizzes(user);
    }

    @PostMapping("/answer")
    public CorrectAnswersDto correctAnswers(Principal principal, @Valid @RequestBody ResultAnswersDto answers) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null){
            return null;
        }

        accessService.create(user, LocalDateTime.now(), "ANSWER");

        answers.setAnswerDate(LocalDateTime.now());

        return statementService.solveQuiz(user, answers);
    }


}