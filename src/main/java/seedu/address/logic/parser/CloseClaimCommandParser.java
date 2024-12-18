package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_CLAIM_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_INSURANCE_ID;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.logic.commands.CloseClaimCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Creates a {@code CloseClaimCommand} object after parsing the user inputs.
 */
public class CloseClaimCommandParser implements Parser<CloseClaimCommand> {
    /**
     * Parses {@code userInput} into a command and returns it for execution.
     *
     * @throws ParseException if {@code userInput} does not conform the expected format
     */
    @Override
    public CloseClaimCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_INSURANCE_ID,
                PREFIX_CLAIM_ID);

        Index index;
        int insuranceId;
        String claimId;

        try {
            if (argMultimap.getValue(PREFIX_INSURANCE_ID).isEmpty()
                    || argMultimap.getValue(PREFIX_CLAIM_ID).isEmpty()) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        CloseClaimCommand.MESSAGE_USAGE));
            }

            index = ParserUtil.parseIndex(argMultimap.getPreamble());
            insuranceId = ParserUtil.parseInsurancePlan(argMultimap.getValue(PREFIX_INSURANCE_ID).get());
            claimId = ParserUtil.parseClaimId(argMultimap.getValue(PREFIX_CLAIM_ID).get());
        } catch (IllegalValueException ive) {
            throw new ParseException(String.format(ive.getMessage(), CloseClaimCommand.MESSAGE_USAGE), ive);
        }

        return new CloseClaimCommand(index, insuranceId, claimId);
    }
}
