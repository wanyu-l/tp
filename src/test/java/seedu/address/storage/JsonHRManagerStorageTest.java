package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.HOON;
import static seedu.address.testutil.TypicalPersons.IDA;
import static seedu.address.testutil.TypicalPersons.getTypicalHrManager;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.DataConversionException;
import seedu.address.model.HrManager;
import seedu.address.model.ReadOnlyHrManager;

public class JsonHRManagerStorageTest {
    private static final Path TEST_DATA_FOLDER =
            Paths.get("src", "test", "data", "JsonAddressBookStorageTest"); //todo

    @TempDir
    public Path testFolder;

    @Test
    public void readHrManager_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> readHrManager(null));
    }

    private java.util.Optional<ReadOnlyHrManager> readHrManager(String filePath) throws Exception {
        return new JsonHRManagerStorage(Paths.get(filePath)).readHrManager(addToTestDataPathIfNotNull(filePath));
    }

    private Path addToTestDataPathIfNotNull(String prefsFileInTestDataFolder) {
        return prefsFileInTestDataFolder != null
                ? TEST_DATA_FOLDER.resolve(prefsFileInTestDataFolder)
                : null;
    }

    @Test
    public void read_missingFile_emptyResult() throws Exception {
        assertFalse(readHrManager("NonExistentFile.json").isPresent()); //todo name of file
    }

    @Test
    public void read_notJsonFormat_exceptionThrown() {
        assertThrows(DataConversionException.class, () -> readHrManager("notJsonFormatAddressBook.json")); //todo name of file
    }

    @Test
    public void readHrManager_invalidPersonHrManager_throwDataConversionException() {
        assertThrows(DataConversionException.class, () -> readHrManager("invalidPersonAddressBook.json")); //todo name of file
    }

    @Test
    public void readHrManager_invalidAndValidPersonHrManager_throwDataConversionException() {
        assertThrows(DataConversionException.class,
                () -> readHrManager("invalidAndValidPersonAddressBook.json")); //todo name of file
    }

    @Test
    public void readAndSaveHrManager_allInOrder_success() throws Exception {
        Path filePath = testFolder.resolve("TempAddressBook.json"); //todo name of file
        HrManager original = getTypicalHrManager();
        JsonHRManagerStorage jsonAddressBookStorage = new JsonHRManagerStorage(filePath);

        // Save in new file and read back
        jsonAddressBookStorage.saveHrManager(original, filePath);
        ReadOnlyHrManager readBack = jsonAddressBookStorage.readHrManager(filePath).get(); //todo name of file
        assertEquals(original, new HrManager(readBack));

        // Modify data, overwrite exiting file, and read back
        original.addPerson(HOON);
        original.removePerson(ALICE);
        jsonAddressBookStorage.saveHrManager(original, filePath);
        readBack = jsonAddressBookStorage.readHrManager(filePath).get();
        assertEquals(original, new HrManager(readBack));

        // Save and read without specifying file path
        original.addPerson(IDA);
        jsonAddressBookStorage.saveHrManager(original); // file path not specified
        readBack = jsonAddressBookStorage.readHrManager().get(); // file path not specified
        assertEquals(original, new HrManager(readBack));

    }

    @Test
    public void saveHrManager_nullHrManager_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveHrManager(null, "SomeFile.json")); //todo name of file
    }

    /**
     * Saves {@code hrManager} at the specified {@code filePath}.
     */
    private void saveHrManager(ReadOnlyHrManager hrManager, String filePath) {
        try {
            new JsonHRManagerStorage(Paths.get(filePath))
                    .saveHrManager(hrManager, addToTestDataPathIfNotNull(filePath));
        } catch (IOException ioe) {
            throw new AssertionError("There should not be an error writing to the file.", ioe);
        }
    }

    @Test
    public void saveHrManager_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveHrManager(new HrManager(), null));
    }
}
