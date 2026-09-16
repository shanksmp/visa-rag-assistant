package com.shanksmp.visaragassistant;

import com.shanksmp.visaragassistant.service.QuestionAnsweringService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Value;
import jakarta.annotation.PostConstruct;

import java.util.List;

@SpringBootTest
public class QuestionAnsweringEvalTest {

    @Autowired
    private QuestionAnsweringService questionAnsweringService;

    record EvalCase(String question, String expectedKeyFact) {
    }

    private final List<EvalCase> evalSet = List.of(
            new EvalCase(
                    "How many days of unemployment am I allowed during STEM OPT?",
                    "150"
            ),
            new EvalCase(
                    "How many total days of unemployment are allowed during the 36-month period with STEM extension?",
                    "150"
            ),
            new EvalCase(
                    "How long is the STEM OPT extension?",
                    "24"
            ),
            new EvalCase(
                    "How many months of OPT can a bachelor's degree student accumulate?",
                    "12"
            ),
            new EvalCase(
                    "How many days before my program end date can I apply for post-completion OPT?",
                    "90"
            ),
            new EvalCase(
                    "How many days after my program end date is the latest I can apply for OPT?",
                    "60"
            ),
            new EvalCase(
                    "Am I required to have a job offer before applying for OPT?",
                    "not necessary"
            ),
            new EvalCase(
                    "How many hours per week must I work during post-completion OPT?",
                    "20"
            ),
            new EvalCase(
                    "What happens to my OPT if I transfer to a different school while it's pending?",
                    "canceled"
            ),
            new EvalCase(
                    "Can I work for multiple employers while on OPT?",
                    "multiple employers"
            ),
            new EvalCase(
                    "What is the cap-gap extension?",
                    "H-1B"
            ),
            new EvalCase(
                    "What is the best way to get advice on H-1B petitions from outside organizations?",
                    "immigration attorney"
            ),
            new EvalCase(
                    "What is the OPT application fee amount in dollars?",
                    "does not"  // expects something like "the document does not specify" or "I don't have"
            ),
            new EvalCase(
                    "Can I apply for OPT while on a J-1 visa?",
                    "F-1"
            )
    );

    @Test
    void runEvalSetAndReportAccuracy() {
        int correct = 0;
        int total = evalSet.size();

        for (EvalCase evalCase : evalSet) {
            String answer = questionAnsweringService.answer(evalCase.question());
            boolean passed = answer.toLowerCase().contains(evalCase.expectedKeyFact().toLowerCase());

            System.out.println((passed ? "PASS" : "FAIL") + " — " + evalCase.question());
            if (!passed) {
                System.out.println("   Expected to contain: \"" + evalCase.expectedKeyFact() + "\"");
                System.out.println("   Actual answer: " + answer);
            }

            if (passed) correct++;
        }

        double accuracy = (double) correct / total * 100;
        System.out.printf("%n=== EVAL RESULTS: %d/%d correct (%.1f%%) ===%n", correct, total, accuracy);
    }
}