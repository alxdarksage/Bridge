package org.sagebionetworks.bridge.webapp.integration.pages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.sagebionetworks.bridge.model.data.value.ParticipantDataDatetimeValue;
import org.sagebionetworks.bridge.webapp.converter.DateToISODateStringConverter;

import com.google.common.collect.Lists;

public class MedicationPage {

	private static final Logger logger = LogManager.getLogger(MedicationPage.class.getName());

	public static final String HEADER = "Medication Tracker";
	private static final long WAIT_PERIOD = 600L;

	protected WebDriverFacade facade;
	protected DataTableInPage activeTable;
	protected DataTableInPage finishedTable;

	public MedicationPage(WebDriverFacade facade) {
		this.facade = facade;
	}

	public void enterNewMedication(String medication, String dose, Date startDate) {
		facade.enterField(".dataRows .medication-value", (medication != null) ? medication : "");
		facade.enterField(".dataRows .dose-value", (dose != null) ? dose : "");
		if (startDate != null) {
			setDate(".dataRows .date-picker", startDate);
		}
		facade.waitFor(WAIT_PERIOD);
	}

	public void enterChangeDose(String dose, Date startDate) {
		facade.enterField("#change-dose .dose-value", dose);
		setDate("#change-dose .date-picker", startDate);
	}

	public void enterEndMed(Date endDate) {
		setDate("#end-med .date-picker", endDate);
	}

	private void setDate(String cssSelector, Date date) {
		String value = "";
		if (date != null) {
			DateToISODateStringConverter converter = new DateToISODateStringConverter();
			ParticipantDataDatetimeValue pdv = new ParticipantDataDatetimeValue();
			pdv.setValue(date.getTime());
			value = converter.convert(pdv).get(0);
		}
		String hiddenDateFormSelector = cssSelector + " + input";
		facade.executeJavaScript("document.querySelector('" + hiddenDateFormSelector + "').value = '" + value + "';");
		facade.executeJavaScript("document.querySelector('" + cssSelector + "').value = '" + value + "';");
	}

	public void clickSubmitNewMedButton() {
		facade.click(".new-med-ok");
	}

	public void clickSubmitNewDoseButton() {
		facade.click("#change-dose .ok");
	}

	public void clickSubmitEndMedButton() {
		facade.click("#end-med .ok");
	}

	public void clickChangeDose(String newMedName) {
		WebElement currentMedRow = getCurrentMedicationRow(newMedName);
		currentMedRow.findElement(By.cssSelector(".med-change-dose")).click();
	}

	public void clickEndMed(String newMedName) {
		WebElement currentMedRow = getCurrentMedicationRow(newMedName);
		currentMedRow.findElement(By.cssSelector(".med-end-med")).click();
	}

	public WebElement getCurrentMedicationRow(String newMedName) {
		List<WebElement> elements = facade.findElements(By.cssSelector("tr[id*='cur-med-']"));
		for (WebElement element : elements) {
			if (newMedName.equals(getField(element, ".medication"))) {
				return element;
			}
		}
		return null;
	}

	public Iterable<WebElement> getHistoricMedicationRows(final String newMedName) {
		List<WebElement> elements = Lists.newArrayList(facade.findElements(By.cssSelector("tr[id*='hist-med-']")));
		String lastMedName = null;
		for (Iterator<WebElement> iterator = elements.iterator(); iterator.hasNext();) {
			WebElement element = iterator.next();
			String medName = getField(element, ".medication");
			if (StringUtils.isEmpty(medName)) {
				medName = lastMedName;
			} else {
				lastMedName = medName;
			}
			if (!newMedName.equals(medName)) {
				iterator.remove();
			}
		}
		return elements;
	}

	public void assertNoCurrentMedication(String newMedName) {
		List<WebElement> elements = facade.findElements(By.cssSelector("tr[id*='cur-med-']"));
		for (WebElement element : elements) {
			WebElement med = element.findElement(By.cssSelector(".medication"));
			if (med.getText().equals(newMedName)) {
				fail(newMedName + " found in current meds, but shouldn't be");
			}
		}
	}

	public void assertEmptyMedWarning() {
		String cssSelector = ".dataRows .medication-value";
		String result = facade.executeJavaScriptForString("return document.querySelector('" + cssSelector + "').checkValidity();");
		assertEquals("false", result);
	}

	public void waitForCurrentMedication(final String newMedName, final String dose, final Date startDate) {
		facade.waitUntil(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver input) {
				WebElement currentMedRow = getCurrentMedicationRow(newMedName);
				if (currentMedRow != null && newMedName.equals(getField(currentMedRow, ".medication"))
						&& dose.equals(getField(currentMedRow, ".dose"))
						&& new SimpleDateFormat("MMM d, yyyy").format(startDate).equals(getField(currentMedRow, ".start"))) {
					return true;
				}
				return false;
			}
		});
	}

	public void waitForHistoricMedication(final String newMedName, final String dose, final Date startDate, final Date endDate) {
		facade.waitUntil(new ExpectedCondition<Boolean>() {
			@Override
			public Boolean apply(WebDriver input) {
				for (WebElement medRow : getHistoricMedicationRows(newMedName)) {
					if (dose.equals(getField(medRow, ".dose"))
							&& new SimpleDateFormat("MMM d, yyyy").format(startDate).equals(getField(medRow, ".start"))
							&& new SimpleDateFormat("MMM d, yyyy").format(endDate).equals(getField(medRow, ".end"))) {
						return true;
					}
				}
				return false;
			}
		});
	}

	private String getField(WebElement parent, String cssSelector) {
		try {
			return parent.findElement(By.cssSelector(cssSelector)).getText();
		} catch (NoSuchElementException e) {
			return null;
		} catch (StaleElementReferenceException e) {
			return null;
		}
	}
}
