package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showClientAtIndex;
import static seedu.address.testutil.TypicalClients.getTypicalAddressBook;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_CLIENT;
import static seedu.address.testutil.TypicalIndexes.INDEX_FOURTH_CLIENT;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_CLIENT;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.LogicManager;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.client.Client;
import seedu.address.model.client.exceptions.ClaimException;
import seedu.address.model.client.exceptions.InsurancePlanException;
import seedu.address.model.client.insurance.InsurancePlan;
import seedu.address.model.client.insurance.claim.Claim;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteClaimCommand}.
 */
public class DeleteClaimCommandTest {

    @TempDir
    public Path temporaryFolder;

    private Model model;
    private LogicManager logic;

    @BeforeEach
    public void setUp() {
        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_validIndexUnfilteredList_success() throws CommandException, InsurancePlanException, ClaimException {

        ModelManager expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        Client clientToEdit = model.getFilteredClientList().get(INDEX_FOURTH_CLIENT.getZeroBased());
        InsurancePlan insurancePlan = clientToEdit.getInsurancePlansManager().getInsurancePlan(0); // Assuming 0 as insurance ID
        Claim claim = clientToEdit.getInsurancePlansManager().getInsurancePlan(0).getClaim("A1001");

        DeleteClaimCommand deleteClaimCommand = new DeleteClaimCommand(INDEX_FOURTH_CLIENT, 0, claim.getClaimId());

        String expectedMessage = String.format(DeleteClaimCommand.MESSAGE_DELETE_CLAIM_SUCCESS,
                clientToEdit.getName().toString(), insurancePlan, claim.getClaimId());

        expectedModel.getFilteredClientList().get(INDEX_FOURTH_CLIENT.getZeroBased())
                .getInsurancePlansManager().deleteClaimFromInsurancePlan(insurancePlan, claim);

        assertCommandSuccess(deleteClaimCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredClientList().size() + 1);
        DeleteClaimCommand deleteClaimCommand = new DeleteClaimCommand(outOfBoundIndex, 0, "B1122");

        assertCommandFailure(deleteClaimCommand, model, Messages.MESSAGE_INVALID_CLIENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() throws CommandException, InsurancePlanException, ClaimException {
        showClientAtIndex(model, INDEX_FOURTH_CLIENT);

        Client clientToEdit = model.getFilteredClientList().get(INDEX_FIRST_CLIENT.getZeroBased());
        InsurancePlan insurancePlan = clientToEdit.getInsurancePlansManager().getInsurancePlan(0);
        Claim claim = insurancePlan.getClaim("B1122");

        DeleteClaimCommand deleteClaimCommand = new DeleteClaimCommand(INDEX_FIRST_CLIENT, 0, claim.getClaimId());

        String expectedMessage = String.format(DeleteClaimCommand.MESSAGE_DELETE_CLAIM_SUCCESS,
                clientToEdit.getName().toString(), insurancePlan.getInsurancePlanId(), claim.getClaimId());

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.getFilteredClientList().get(INDEX_FIRST_CLIENT.getZeroBased())
                .getInsurancePlansManager().deleteClaimFromInsurancePlan(insurancePlan, claim);
        showNoClient(expectedModel);

        assertCommandSuccess(deleteClaimCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showClientAtIndex(model, INDEX_FOURTH_CLIENT);

        Index outOfBoundIndex = INDEX_SECOND_CLIENT;
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getClientList().size());

        DeleteClaimCommand deleteClaimCommand = new DeleteClaimCommand(outOfBoundIndex, 0, "B1122");

        assertCommandFailure(deleteClaimCommand, model, Messages.MESSAGE_INVALID_CLIENT_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        DeleteClaimCommand deleteFirstCommand = new DeleteClaimCommand(INDEX_FIRST_CLIENT, 0, "B1234");
        DeleteClaimCommand deleteSecondCommand = new DeleteClaimCommand(INDEX_SECOND_CLIENT, 1, "B5678");

        // same object -> returns true
        assertEquals(deleteFirstCommand, deleteFirstCommand);

        // same values -> returns true
        DeleteClaimCommand deleteFirstCommandCopy = new DeleteClaimCommand(INDEX_FIRST_CLIENT, 0, "B1234");
        assertEquals(deleteFirstCommand, deleteFirstCommandCopy);

        // different types -> returns false
        assertNotEquals(1, deleteFirstCommand);

        // null -> returns false
        assertNotEquals(null, deleteFirstCommand);

        // different client and claim -> returns false
        assertNotEquals(deleteFirstCommand, deleteSecondCommand);
    }

    /**
     * Updates {@code model}'s filtered list to show no one.
     */
    private void showNoClient(Model model) {
        model.updateFilteredClientList(p -> false);

        assertTrue(model.getFilteredClientList().isEmpty());
    }
}
