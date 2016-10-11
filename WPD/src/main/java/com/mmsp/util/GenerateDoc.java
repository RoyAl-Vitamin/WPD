package com.mmsp.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.apache.commons.lang3.StringUtils;
import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.P;
import org.docx4j.wml.Text;

import com.mmsp.model.WPDVersion;

/**
 * Work only with *.docx
 * @author rav
 *
 */
public class GenerateDoc {

	private WPDVersion version = null;

	private String tokenThematicalPlan = "Thematical_Plan";

	// UNDONE
	// http://www.smartjava.org/content/create-complex-word-docx-documents-programatically-docx4j
	public void generate(WPDVersion currWPDVersion) {
		version = currWPDVersion;

		System.out.println("Начинаю генерацию");
		String pathToTemplateFile = version.getTemplateName(); // путь до шаблона
		File fInput = new File(pathToTemplateFile);
		System.err.println("File exist == " + fInput.exists() + "\nand this path " + fInput.getAbsolutePath());
		WordprocessingMLPackage wordMLPackage = null;
		try {
			wordMLPackage = WordprocessingMLPackage.load(fInput);
		} catch (Docx4JException e) {
			System.err.println("Не удалось найти шаблон");
			e.printStackTrace();
		}

		// Замена параграфа
		String placeholder = "I_SEARCH"; // Что ищем
		String toAdd = "THIS_PHRASE"; // на что заменяем
 
		replaceParagraph(placeholder, toAdd, wordMLPackage, wordMLPackage.getMainDocumentPart());
		addThematicalPlan(wordMLPackage, wordMLPackage.getMainDocumentPart());

		String pathToGenFile = pathToTemplateFile.substring(0, pathToTemplateFile.lastIndexOf(".")) + "_gen" + pathToTemplateFile.substring(pathToTemplateFile.lastIndexOf(".")); // путь до сгенерированного файла
		File fOutput = new File(pathToGenFile);
		try {
			wordMLPackage.save(fOutput);
		} catch (Docx4JException e) {
			System.err.println("Не удалось сохранить сгенирированый файл");
			e.printStackTrace();
		}
		System.out.println("Заканчиваю генерацию");
	}

	/**
	 * Находит Thematical_Plan и заменяет её на Тематический план
	 * @param wordMLPackage хз
	 * @param mainDocumentPart хз что это
	 * 
	 * Структура следующая (2 пробела - возможно несколько объектов):
	 * <p><b>Семестр #.</b></p>
	 *   <p><b>Модуль #. #{описание}</b></p>
	 *   <p>#{часы}</p>
	 *     <p><b>Раздел #. #{описание}</b></p>
	 *     <p>#{часы}</p>
	 *       <p>Тема #</p>
	 *       <p>#{описание}</p>
	 * 
	 * TODO Возможно стоит сделать Введение?
	 * TODO Нужен ли семестр?
	 */
	private void addThematicalPlan(WordprocessingMLPackage document, ContentAccessor addTo) {
		List<Object> paragraphs = getAllElementFromObject(document.getMainDocumentPart(), P.class);

		// Находим позицию для вставки
		P toReplace = null;
		int pos = -1;
		for (Object p : paragraphs) {
			List<Object> texts = getAllElementFromObject(p, Text.class);
			for (Object t : texts) {
				Text content = (Text) t;
				if (content.getValue().equals(tokenThematicalPlan)) {
					toReplace = (P) p;
					pos = addTo.getContent().indexOf(p); // запоминаем позицию
					System.out.println("Позиция параграфа, который содержит tokenThematicalPlan " + tokenThematicalPlan + ", == "+ pos);
					break;
				}
			}
		}

		List<P> listOfParagraph = new ArrayList<P>();
		for (int i = 0; i < version.getTreeSemesters().size(); i++) {
			P Paragraph = new P(); // вставка инорамации про семестр
		}
	}

	private static List<Object> getAllElementFromObject(Object obj, Class<?> toSearch) {
		List<Object> result = new ArrayList<Object>();
		if (obj instanceof JAXBElement) obj = ((JAXBElement<?>) obj).getValue();
 
		if (obj.getClass().equals(toSearch))
			result.add(obj);
		else if (obj instanceof ContentAccessor) {
			List<?> children = ((ContentAccessor) obj).getContent();
			for (Object child : children) {
				result.addAll(getAllElementFromObject(child, toSearch));
			}
 
		}
		return result;
	}

	private void replaceParagraph(String placeholder, String textToAdd, WordprocessingMLPackage template, ContentAccessor addTo) {
		// 1. get the paragraph
		List<Object> paragraphs = getAllElementFromObject(template.getMainDocumentPart(), P.class);
 
		P toReplace = null;
		int pos = -1; // position replacement
		for (Object p : paragraphs) {
			List<Object> texts = getAllElementFromObject(p, Text.class);
			for (Object t : texts) {
				Text content = (Text) t;
				if (content.getValue().equals(placeholder)) {
					toReplace = (P) p;
					pos = addTo.getContent().indexOf(p);
					System.out.println("Позиция параграфа, который ищу == " + pos);
					break;
				}
			}
		}
 
		// we now have the paragraph that contains our placeholder: toReplace
		// 2. split into seperate lines
		String as[] = StringUtils.splitPreserveAllTokens(textToAdd, '\n');
 
		for (int i = 0; i < as.length; i++) {
			String ptext = as[i];
 
			// 3. copy the found paragraph to keep styling correct
			P copy = (P) XmlUtils.deepCopy(toReplace);
 
			// replace the text elements from the copy
			List<?> texts = getAllElementFromObject(copy, Text.class);
			if (texts.size() > 0) {
				Text textToReplace = (Text) texts.get(0);
				textToReplace.setValue(ptext);
			}
 
			// add the paragraph to the document
			if (pos > -1)
				addTo.getContent().add(pos, copy);
			else
				addTo.getContent().add(copy);
		}
 
		// 4. remove the original one
		((ContentAccessor)toReplace.getParent()).getContent().remove(toReplace);
 
	}

}
