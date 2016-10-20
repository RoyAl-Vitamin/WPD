package com.mmsp.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.docx4j.XmlUtils;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.Text;
import org.docx4j.wml.ObjectFactory;

import com.mmsp.model.Module;
import com.mmsp.model.Section;
import com.mmsp.model.Semester;
import com.mmsp.model.ThematicPlan;
import com.mmsp.model.WPDVersion;

/**
 * Work only with *.docx
 * @author rav
 */
public class GenerateDoc {

	private WPDVersion version = null;

	private String tokenThematicalPlan = "Thematical_Plan";

	private ObjectFactory factory = null;

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

		factory = Context.getWmlObjectFactory();
		// Замена параграфа
		//String placeholder = "I_SEARCH"; // Что ищем
		//String toAdd = "THIS_PHRASE"; // на что заменяем
 
		//replaceParagraph(placeholder, toAdd, wordMLPackage, wordMLPackage.getMainDocumentPart());
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
	 * @param document
	 * @param addTo
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
	 * TODO Возможно стоит сделать "Введение"? В отдельном окне или в "Таблице 7.1"?
	 * TODO Нужен ли "Семестр"?
	 * FIXME Необходим BOLD в некторых местах, а так же межстрочный интервал и между разделами просто перенос строки
	 * FIXME Перевести в отдельный поток, а то тормозит весь процесс
	 * FIXME Сделать конкатенацию для часов
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

		// Создаём граф семестров
		List<P> listOfParagraph = new ArrayList<P>();

		for (Semester sem: version.getTreeSemesters()) {
			// Семестр и его номер
			P pSemester = factory.createP(); // вставка инорамации про семестр
			R rSemester = factory.createR();
			Text tSemester = factory.createText();
			tSemester.setValue("Семестр " + sem.getNUMBER_OF_SEMESTER() + "."); // TODO Сделать эту строку Bold
			rSemester.getContent().add(tSemester);
			pSemester.getContent().add(rSemester);
			listOfParagraph.add(pSemester);

			for (Module mod : sem.getTreeModule()) {
				// Модуль, номер и его описание
				P pModule = factory.createP(); // вставка ифнорамации про семестр
				R rModule = factory.createR();
				Text tModule = factory.createText();
				tModule.setValue("Модуль " + mod.getNumber() + ". " + mod.getName()); // TODO Сделать эту строку Bold
				rModule.getContent().add(tModule);
				pModule.getContent().add(rModule);
				listOfParagraph.add(pModule);
				// Часы на модуль
				P pTimeForModule = factory.createP();
				R rTimeForModule = factory.createR();
				Text tTimeForModule = factory.createText();

				String sTimeForModule = ""; // TODO написать функцию конкатинации строк, если сейчас Л == 0, то следующая строка начнётся с запятой
				if (mod.getL() != 0) sTimeForModule += " Л - " + mod.getL() + " час";
				if (mod.getPZ() != 0) sTimeForModule += ", ПЗ - " + mod.getPZ() + " час";
				if (mod.getLR() != 0) sTimeForModule += ", ЛР - " + mod.getLR() + " час";
				if (mod.getKSR() != 0) sTimeForModule += ", КСР - " + mod.getKSR() + " час";
				if (mod.getSRS() != 0) sTimeForModule += ", СРС - " + mod.getSRS() + " час.";

				tTimeForModule.setValue(sTimeForModule);
				rTimeForModule.getContent().add(tTimeForModule);
				pTimeForModule.getContent().add(rTimeForModule);
				listOfParagraph.add(pTimeForModule);

				for (Section sec : mod.getTreeSection()) {
					// Раздел, номер и его описание
					P pSection = factory.createP();
					R rSection = factory.createR();
					Text tSection = factory.createText();
					tSection.setValue("Раздел " + sec.getNumber() + ". " + sec.getName()); // TODO Сделать Только "Модуль" и его номер Bold
					rSection.getContent().add(tSection);
					pSection.getContent().add(rSection);
					listOfParagraph.add(pSection);
					// Часы на раздел
					P pTimeForSection = factory.createP();
					R rTimeForSection = factory.createR();
					Text tTimeForSetion = factory.createText();

					String sTimeForSectoin = ""; // TODO написать функцию конкатинации строк, если сейчас Л == 0, то следующая строка начнётся с запятой
					if (sec.getL() != 0) sTimeForSectoin += " Л - " + sec.getL() + " час";
					if (sec.getPZ() != 0) sTimeForSectoin += ", ПЗ - " + sec.getPZ() + " час";
					if (sec.getLR() != 0) sTimeForSectoin += ", ЛР - " + sec.getLR() + " час";
					if (sec.getKSR() != 0) sTimeForSectoin += ", КСР - " + sec.getKSR() + " час";
					if (sec.getSRS() != 0) sTimeForSectoin += ", СРС - " + sec.getSRS() + " час.";

					tTimeForSetion.setValue(sTimeForSectoin);
					rTimeForSection.getContent().add(tTimeForSetion);
					pTimeForSection.getContent().add(rTimeForSection);
					listOfParagraph.add(pTimeForSection);

					for (ThematicPlan theme : sec.getTreeTheme()) {
						// Тема, номер и её описание
						P pTheme = factory.createP();
						R rTheme = factory.createR();
						Text tTheme = factory.createText();
						tTheme.setValue("Тема " + theme.getNumber() + ".");
						rTheme.getContent().add(tTheme);
						pTheme.getContent().add(rTheme);
						listOfParagraph.add(pTheme);

						P pThemeDesc = factory.createP();
						R rThemeDesc = factory.createR();
						Text tThemeDesc = factory.createText();
						tThemeDesc.setValue(theme.getTitle());
						rThemeDesc.getContent().add(tThemeDesc);
						pThemeDesc.getContent().add(rThemeDesc);
						listOfParagraph.add(pThemeDesc);
					}
				}
				// Раздел, номер и его описание
				P pEmpty = factory.createP();
				R rEmpty = factory.createR();
				Text tEmpty = factory.createText();
				tEmpty.setValue("");
				rEmpty.getContent().add(tEmpty);
				pEmpty.getContent().add(rEmpty);
				listOfParagraph.add(pEmpty);
			}
		}

		// Вставляем в указанную позицию весь массив listOfParagraph
		if (pos > -1) {
			int i_pos = pos;
			for (P p : listOfParagraph)
				addTo.getContent().add(i_pos++, p);
		} else {
			for (P p : listOfParagraph)
				addTo.getContent().add(p);
		}
		((ContentAccessor)toReplace.getParent()).getContent().remove(toReplace); // Удаление шаблонной фразы
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

	/*private void replaceParagraph(String placeholder, String textToAdd, WordprocessingMLPackage template, ContentAccessor addTo) {
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
 
	}*/

}
