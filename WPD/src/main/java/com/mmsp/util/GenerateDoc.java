package com.mmsp.util;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.BooleanDefaultTrue;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.HpsMeasure;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase.Spacing;
import org.docx4j.wml.R;
import org.docx4j.wml.RFonts;
import org.docx4j.wml.RPr;
import org.docx4j.wml.STLineSpacingRule;
import org.docx4j.wml.Text;

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
    
    static final Logger log = LogManager.getLogger(GenerateDoc.class);

	private WPDVersion version = null;

	private String tokenThematicalPlan = "Thematical_Plan";

	private ObjectFactory factory = null;

	// UNDONE
	// http://www.smartjava.org/content/create-complex-word-docx-documents-programatically-docx4j
	public void generate(WPDVersion currWPDVersion) {
		version = currWPDVersion;

		log.debug("Начинаю генерацию");
		// путь до шаблона
		String pathToTemplateFile = version.getTemplateName();
		File fInput = new File(pathToTemplateFile);
		log.debug("File exist == " + fInput.exists() + "\nand this path " + fInput.getAbsolutePath());
		WordprocessingMLPackage wordMLPackage = null;
		try {
			wordMLPackage = WordprocessingMLPackage.load(fInput);
		} catch (Docx4JException e) {
			log.error("Не удалось найти шаблон");
			return;
		}

		factory = Context.getWmlObjectFactory();
		// Замена параграфа
		//String placeholder = "I_SEARCH"; // Что ищем
		//String toAdd = "THIS_PHRASE"; // на что заменяем
 
		//replaceParagraph(placeholder, toAdd, wordMLPackage, wordMLPackage.getMainDocumentPart());
		addThematicalPlan(wordMLPackage, wordMLPackage.getMainDocumentPart());

		// путь до сгенерированного файла
		String pathToGenFile = pathToTemplateFile.substring(0, pathToTemplateFile.lastIndexOf(".")) + "_gen" + pathToTemplateFile.substring(pathToTemplateFile.lastIndexOf("."));
		File fOutput = new File(pathToGenFile);
		try {
			wordMLPackage.save(fOutput);
		} catch (Docx4JException e) {
			log.error("Не удалось сохранить сгенирированый файл");
			e.printStackTrace();
		}
		log.debug("Заканчиваю генерацию");
	}

	/**
	 * Задаёт
	 * @param fontSize размер шрифта
	 * @param fontName название шрифта
	 * @param fontBold жирность шрифта
	 * @return {@link RPr}
	 */
	private RPr getRPr(int fontSize, String fontName, boolean fontBold) {
	    RPr rpr = factory.createRPr();
	    
        // Установка шрифта
        RFonts rf = factory.createRFonts();
        rf.setAscii(fontName);
        // Установка размера шрифта
        HpsMeasure size = new HpsMeasure();
        size.setVal(BigInteger.valueOf(fontSize * 2));
        // Установка жирности шрифта
        BooleanDefaultTrue b = new BooleanDefaultTrue();
        b.setVal(fontBold);

        rpr.setRFonts(rf);
        rpr.setSz(size);
        rpr.setB(b);

	    return rpr;
	}
	
	/**
	 * Задаёт межстрочный интервал
	 * @param spac
	 * @return {@link Spacing}
	 */
	private Spacing getSpacing(float spac) {
	    Spacing spacing = factory.createPPrBaseSpacing();
        spacing.setLine(BigInteger.valueOf((long) (spac * 240)));
        spacing.setLineRule(STLineSpacingRule.AUTO);
        return spacing;
	}
	
	/**
	 * Находит tokenThematicalPlan и заменяет её на Тематический план
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
	 * FIXME Перевести в отдельный поток, а то тормозит весь процесс
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
					log.debug("Позиция параграфа, который содержит [tokenThematicalPlan == " + tokenThematicalPlan + "], == " + pos);
					break;
				}
			}
		}

		// Создаём граф семестров
		List<P> listOfParagraph = new ArrayList<P>();

		for (Semester sem: version.getTreeSemesters()) {
			// Семестр и его номер
			P pSemester = factory.createP();
			PPr ppr = factory.createPPr();
		    ppr.setSpacing(getSpacing(1.5f));
			pSemester.setPPr(ppr);
			
			R rSemester = factory.createR();
			Text tSemester = factory.createText();

			tSemester.setValue("Семестр " + sem.getNUMBER_OF_SEMESTER() + ".");
			rSemester.setRPr(getRPr(14, "Times New Roman", true));
			rSemester.getContent().add(tSemester);
			pSemester.getContent().add(rSemester);
			
			listOfParagraph.add(pSemester);

			for (Module mod : sem.getTreeModule()) {
				// Модуль, номер и его описание
				P pModule = factory.createP(); // вставка ифорамации про семестр
				R rModule = factory.createR();
				Text tModule = factory.createText();
	            pModule.setPPr(ppr);

				tModule.setValue("Модуль " + mod.getNumber() + ". " + mod.getName());
				rModule.setRPr(getRPr(14, "Times New Roman", true));
				rModule.getContent().add(tModule);
				pModule.getContent().add(rModule);
				listOfParagraph.add(pModule);
				// Часы на модуль
				P pTimeForModule = factory.createP();
				R rTimeForModule = factory.createR();
				Text tTimeForModule = factory.createText();

				StringBuilder sbTimeForModule = new StringBuilder();
				if (mod.getL() != 0) sbTimeForModule.append(" Л - ").append(mod.getL()).append(" ч.,");
				if (mod.getPZ() != 0) sbTimeForModule.append(" ПЗ - ").append(mod.getPZ()).append(" ч.,");
				if (mod.getLR() != 0) sbTimeForModule.append(" ЛР - ").append(mod.getLR()).append(" ч.,");
				if (mod.getKSR() != 0) sbTimeForModule.append(" КСР - ").append(mod.getKSR()).append(" ч.,");
				if (mod.getSRS() != 0) sbTimeForModule.append(" СРС - ").append(mod.getSRS()).append(" ч.,");
				if (sbTimeForModule.length() > 1 && ',' == sbTimeForModule.charAt(sbTimeForModule.length() - 1)) {
				    sbTimeForModule.deleteCharAt(sbTimeForModule.length() - 1);
				}

				pTimeForModule.setPPr(ppr);
				tTimeForModule.setValue(sbTimeForModule.toString());
				rTimeForModule.setRPr(getRPr(14, "Times New Roman", false));
				rTimeForModule.getContent().add(tTimeForModule);
				rTimeForModule.getContent().add(factory.createBr());
				pTimeForModule.getContent().add(rTimeForModule);
				listOfParagraph.add(pTimeForModule);

                // Раздел, номер и его описание
				for (Section sec : mod.getTreeSection()) {
					// Раздел, номер и его описание
					P pSection = factory.createP();
					R rSectionNumber = factory.createR();
					R rSectionDescription = factory.createR();
					Text tSectionNumber = factory.createText();
					Text tSectionDescription = factory.createText();
					pSection.setPPr(ppr);
                    rSectionNumber.setRPr(getRPr(14, "Times New Roman", true));
                    rSectionDescription.setRPr(getRPr(14, "Times New Roman", false));
					

					tSectionNumber.setValue("Раздел " + sec.getNumber() + ". ");
					tSectionDescription.setValue(" " + sec.getName());
					rSectionNumber.getContent().add(tSectionNumber);
					rSectionDescription.getContent().add(tSectionDescription);

					pSection.getContent().add(rSectionNumber);
					pSection.getContent().add(rSectionDescription);
					listOfParagraph.add(pSection);
					// Часы на раздел
					P pTimeForSection = factory.createP();
					R rTimeForSection = factory.createR();
					Text tTimeForSetion = factory.createText();

					pTimeForSection.setPPr(ppr);
                    rTimeForSection.setRPr(getRPr(14, "Times New Roman", false));

					StringBuilder sbTimeForSectoin = new StringBuilder();
					if (sec.getL() != 0) sbTimeForSectoin.append(" Л - ").append(sec.getL()).append(" ч.,");
					if (sec.getPZ() != 0) sbTimeForSectoin.append(" ПЗ - ").append(sec.getPZ()).append(" ч.,");
					if (sec.getLR() != 0) sbTimeForSectoin.append(" ЛР - ").append(sec.getLR()).append(" ч.,");
					if (sec.getKSR() != 0) sbTimeForSectoin.append(" КСР - ").append(sec.getKSR()).append(" ч.,");
    				if (sec.getSRS() != 0) sbTimeForSectoin.append(" СРС - ").append(sec.getSRS()).append(" ч.,");
	                if (sbTimeForSectoin.length() > 1 && ',' == sbTimeForSectoin.charAt(sbTimeForSectoin.length() - 1)) {
	                    sbTimeForSectoin.deleteCharAt(sbTimeForSectoin.length() - 1);
	                }

					tTimeForSetion.setValue(sbTimeForSectoin.toString());
					rTimeForSection.getContent().add(tTimeForSetion);
					pTimeForSection.getContent().add(rTimeForSection);
					listOfParagraph.add(pTimeForSection);

					for (ThematicPlan theme : sec.getTreeTheme()) {
						// Тема, номер и её описание
						P pTheme = factory.createP();
						R rTheme = factory.createR();
						Text tTheme = factory.createText();
	                    rTheme.setRPr(getRPr(14, "Times New Roman", false));

						tTheme.setValue("Тема " + theme.getNumber() + ".");
						rTheme.getContent().add(tTheme);
						pTheme.getContent().add(rTheme);
						pTheme.setPPr(ppr);
						listOfParagraph.add(pTheme);

						P pThemeDesc = factory.createP();
						R rThemeDesc = factory.createR();
						Text tThemeDesc = factory.createText();
						
                        rThemeDesc.setRPr(getRPr(12, "Times New Roman", false));
						tThemeDesc.setValue(theme.getTitle());
						rThemeDesc.getContent().add(tThemeDesc);
						pThemeDesc.getContent().add(rThemeDesc);
						pThemeDesc.setPPr(ppr);
						listOfParagraph.add(pThemeDesc);
					}
					
					P pEmpty = factory.createP();
	                R rEmpty = factory.createR();
//	                rEmpty.getContent().add(factory.createBr());
	                pEmpty.getContent().add(rEmpty);
	                pEmpty.setPPr(ppr);
	                listOfParagraph.add(pEmpty);
				}
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
