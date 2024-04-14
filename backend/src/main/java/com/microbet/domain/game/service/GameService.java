package com.microbet.domain.game.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ImmediateRefreshHandler;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.microbet.domain.game.domain.Team;
import com.microbet.domain.game.enums.TeamName;
import com.microbet.domain.game.repository.GameRepository;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;

    private static final String baseUrl = "https://sports.daum.net/baseball";

    public void scrapGameInfo() {

        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);

        try {
            HtmlPage page = webClient.getPage(baseUrl);

            webClient.waitForBackgroundJavaScript(2000);
            List<HtmlElement> teamList = page
                    .getByXPath("//div[contains(@class, 'team_left') or contains(@class, 'team_right')]");

            teamList.forEach((item) -> {
                System.out.println(createTeam(item).toString());
            });

            webClient.close();
        } catch (IOException e) {

        }
    }

    private Team createTeam(HtmlElement item) {        
        Map<String, String> teamMap = Map.of(
            "LG", "LG",
            "KT", "KT",
            "SSG", "SSG",
            "NC", "NC",
            "두산","DOOSAN", 
            "KIA", "KIA",
            "롯데","LOTTE", 
            "삼성","SAMSUNG", 
            "한화","HANWHA", 
            "키움", "KIWOOM"
        );

        HtmlElement teamNameElement = (HtmlElement) item.getFirstByXPath(".//span[@class='inner_tit']");
        
        // HtmlElement teamScoreElement = (HtmlElement)
        // item.getFirstByXPath(".//em[@class='num_score']");
        final String teamAlias = teamNameElement.getTextContent();

        return Team.builder()
                .name(TeamName.valueOf(teamMap.get(teamAlias)))
                .alias(teamAlias)
                .build();
    }
}
