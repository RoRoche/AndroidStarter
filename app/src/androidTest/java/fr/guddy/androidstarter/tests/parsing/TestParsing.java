package fr.guddy.androidstarter.tests.parsing;

import android.support.test.InstrumentationRegistry;
import android.test.suitebuilder.annotation.LargeTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.polok.localify.LocalifyClient;

import org.frutilla.FrutillaTestRunner;
import org.frutilla.annotations.Frutilla;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;

import fr.guddy.androidstarter.rest.dto.DTORepo;
import fr.guddy.androidstarter.test.R;

import static com.google.common.truth.Truth.assertThat;

@RunWith(FrutillaTestRunner.class)
@LargeTest
public class TestParsing {

    //region Fields
    private LocalifyClient mLocalifyClient;
    //endregion

    //region Test lifecycle
    @Before
    public void setUp() throws Exception {
        mLocalifyClient = new LocalifyClient.Builder()
                .withResources(InstrumentationRegistry.getContext().getResources())
                .build();
    }
    //endregion

    //region Test methods
    @Frutilla(
            Given = "A single GitHub repo from a JSON file",
            When = "Parsing this content with Jackson",
            Then = "It should have parsed a repo named \"git-consortium\""
    )
    @Test
    public void test_Parse_JSONRepo_ParsesRepoDTO() throws IOException {
        ObjectMapper loJSONMapper;
        String lsRepoData;
        Given:
        {
            loJSONMapper = new ObjectMapper();
            lsRepoData = mLocalifyClient.localify().loadRawFile(R.raw.repo_octocat);
        }

        DTORepo loRepoDTO;
        When:
        {
            loRepoDTO = loJSONMapper.readValue(lsRepoData, DTORepo.class);
        }

        Then:
        {
            assertThat(loRepoDTO).isNotNull();
            assertThat(loRepoDTO.name).isEqualTo("git-consortium");
        }
    }

    public static final class DTORepos extends ArrayList<DTORepo> {
    }

    @Frutilla(
            Given = "Multiple GitHub repos from a JSON file",
            When = "Parsing this content with Jackson",
            Then = "It should have parsed a repo named \"git-consortium\""
    )
    @Test
    public void test_Parse_JSONArrayRepo_ParsesRepoAsArrayDTO() throws IOException {
        ObjectMapper loJSONMapper;
        String lsRepoDataAsArray;
        Given:
        {
            loJSONMapper = new ObjectMapper();
            lsRepoDataAsArray = mLocalifyClient.localify().loadRawFile(R.raw.repos_octocat);
        }

        DTORepos lloRepoAsArrayDTO;
        When:
        {
            lloRepoAsArrayDTO = loJSONMapper.readValue(lsRepoDataAsArray, DTORepos.class);
        }

        Then:
        {
            assertThat(lloRepoAsArrayDTO).hasSize(1);
            assertThat(lloRepoAsArrayDTO.get(0).name).isEqualTo("git-consortium");
        }
    }
    //endregion
}
