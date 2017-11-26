package br.unirio.dsw.selecaoppgi.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.itextpdf.text.Chapter;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import br.unirio.dsw.selecaoppgi.model.edital.Edital;
import br.unirio.dsw.selecaoppgi.model.edital.ProjetoPesquisa;
import br.unirio.dsw.selecaoppgi.model.edital.ProvaEscrita;
import br.unirio.dsw.selecaoppgi.model.inscricao.AvaliacaoProvaEscrita;
import br.unirio.dsw.selecaoppgi.model.inscricao.InscricaoEdital;
import br.unirio.dsw.selecaoppgi.service.dao.EditalDAO;
import br.unirio.dsw.selecaoppgi.service.dao.InscricaoDAO;
import br.unirio.dsw.selecaoppgi.service.dao.UsuarioDAO;

@Controller
public class RelatorioController {
	// /relatorio/homologacao/homologacao/original
	// /relatorio/homologacao/homologacao/recurso
	// /relatorio/homologacao/dispensa/original
	// /relatorio/homologacao/dispensa/recurso
	// /relatorio/escritas/presenca
	// /relatorio/escritas/notas/original
	// /relatorio/escritas/notas/recurso
	// /relatorio/escritas/pendencias
	// /relatorio/alinhamento/presenca
	// /relatorio/alinhamento/notas/original
	// /relatorio/alinhamento/notas/recurso
	// /relatorio/alinhamento/pendencias
	// /relatorio/aprovacao

	@Autowired
	private InscricaoDAO inscricaoDAO;
	
	@Autowired
	private UsuarioDAO userDAO;

	@Autowired
	private EditalDAO editalDAO;

	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/edital/{id}/relatorio/escritas/presenca", method = RequestMethod.GET)
	public void presencaProvaEscrita(@PathVariable int id, HttpServletResponse response)
			throws DocumentException, IOException {

		Edital edital = editalDAO.carregaEditalId(id, userDAO);
		
		List<ProvaEscrita> provasEscritas = (List<ProvaEscrita>) edital.getProvasEscritas();
		
//		List<InscricaoEdital> inscritos = new ArrayList<>();
//		InscricaoEdital inscricao1 = new InscricaoEdital(edital);
//		inscricao1.setNomeCandidato("fulano1");
//		
//		
//		AvaliacaoProvaEscrita ape1 = new AvaliacaoProvaEscrita(provasEscritas.get(0));
//		
//		ProjetoPesquisa pp = new ProjetoPesquisa();
//		
//		ProvaEscrita prova = new ProvaEscrita();
//		prova.setCodigo("FSI");
//		pp.adicionaProvaEscrita(prova);
//		
//		inscricao1.adicionaInscricaoProjetoPesquisa(pp, "teste123");
//		
//		inscritos.add(inscricao1);
		
		List<InscricaoEdital> inscritos = inscricaoDAO.carregaInscricoesEditalAcessoPublico(edital);

		Document document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfWriter.getInstance(document, baos);

		document.open();
		
		document.addTitle("Lista de presença em prova escrita");
		Font chapterFont = FontFactory.getFont(FontFactory.HELVETICA, 16, Font.BOLD);
		Font paragraphFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL);
		Chunk chunk = new Chunk("Lista de presença em prova escrita \n" + edital.getNome(), chapterFont);
//		Chapter chapter = new Chapter(new Paragraph(chunk), 1);
//		chapter.setNumberDepth(0);
//		document.add(chapter);
		
//		provasEscritas.forEach(provaEscrita -> {
		int i=0;
		for(ProvaEscrita provaEscrita : provasEscritas) {
			Chapter chap = new Chapter(new Paragraph(chunk), ++i);

			chap.add(new Paragraph(provaEscrita.getNome(), paragraphFont));
			inscritos.forEach(inscrito -> {
				inscrito.getAvaliacoesProvasEscritas().forEach(provaDoInscrito -> {
					if(provaEscrita.getCodigo().equals(provaDoInscrito.getProvaEscrita().getCodigo()))
						chap.add(new Paragraph(inscrito.getNomeCandidato() + " __________________________________________________"));
				});
			});
			
			try {
				document.add(chap);
				document.newPage();
			} catch (DocumentException e) {
				e.printStackTrace();
			}
		};

		document.close();

		response.setHeader("Expires", "0");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
		response.setHeader("Pragma", "public");
		response.setContentType("application/pdf");
		response.setContentLength(baos.size());
		OutputStream os = response.getOutputStream();
		baos.writeTo(os);
		os.flush();
		os.close();

	}

}