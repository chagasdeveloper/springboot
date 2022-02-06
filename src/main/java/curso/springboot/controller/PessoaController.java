package curso.springboot.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import curso.springboot.model.Pessoa;
import curso.springboot.model.Telefone;
import curso.springboot.repository.PessoaRepository;
import curso.springboot.repository.ProfissaoRepository;
import curso.springboot.repository.TelefoneRepository;

@Controller
public class PessoaController {
	@Autowired
	PessoaRepository pessoaRepository;
	@Autowired
	TelefoneRepository telefoneRepository;
	@Autowired
	ReportUtil reportUtil;
	@Autowired
	private ProfissaoRepository profissaoRepository;

	@RequestMapping(value = "/cadastropessoa", method = RequestMethod.GET)
	public ModelAndView inicio() {
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		modelAndView.addObject("pessoaobj", new Pessoa());
		modelAndView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
		modelAndView.addObject("profissoes", profissaoRepository.findAll());
		return modelAndView;
	}

	@RequestMapping(method = RequestMethod.POST, value = "**/salvarpessoa", consumes = { "multipart/form-data" })
	public ModelAndView salvar(@Valid Pessoa pessoa, BindingResult bindingResult, final MultipartFile file)
			throws IOException {
		pessoa.setTelefones(telefoneRepository.getTelefones(pessoa.getId()));
		if (bindingResult.hasErrors()) {
			ModelAndView andView = new ModelAndView("cadastro/cadastropessoa");
			andView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
			andView.addObject("pessoaobj", pessoa);
			andView.addObject("profissoes", profissaoRepository.findAll());
			List<String> msg = new ArrayList<String>();
			for (ObjectError objectError : bindingResult.getAllErrors()) {
				msg.add(objectError.getDefaultMessage()); // vem das anotações @NotEmpty e @NotNull e outras
			}
			andView.addObject("msg", msg);
			return andView;
		}
		if (file.getSize() > 0) {
			pessoa.setCurriculo(file.getBytes());
			pessoa.setTipoFileCurriculo(file.getContentType());
			pessoa.setNomeFileCurriculo(file.getOriginalFilename());
		} else {
			if (pessoa.getId() != null && pessoa.getId() > 0) { // editando
				Pessoa pessoaTemp = pessoaRepository.findById(pessoa.getId()).get();
				pessoa.setCurriculo(pessoaTemp.getCurriculo());
				pessoa.setNomeFileCurriculo(pessoaTemp.getNomeFileCurriculo());
				pessoa.setTipoFileCurriculo(pessoaTemp.getTipoFileCurriculo());
			}
		}
		pessoaRepository.save(pessoa);
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");

		modelAndView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
		modelAndView.addObject("pessoaobj", new Pessoa());
		modelAndView.addObject("profissoes", profissaoRepository.findAll());
		return modelAndView;

	}

	@RequestMapping(method = RequestMethod.GET, value = "**/listapessoas")
	public ModelAndView pessoas() {
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");

		modelAndView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
		modelAndView.addObject("pessoaobj", new Pessoa());
		return modelAndView;
	}

	@GetMapping("**/editarpessoa/{idpessoa}")
	public ModelAndView editar(@PathVariable("idpessoa") Long idpessoa) {
		Optional<Pessoa> pessoa = pessoaRepository.findById(idpessoa);
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		modelAndView.addObject("pessoaobj", pessoa.get());
		modelAndView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
		modelAndView.addObject("profissoes", profissaoRepository.findAll());
		return modelAndView;
	}

	@GetMapping("**/removerpessoa/{idpessoa}")
	public ModelAndView excluir(@PathVariable("idpessoa") Long idpessoa) {
		pessoaRepository.deleteById(idpessoa);
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		modelAndView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
		modelAndView.addObject("pessoaobj", new Pessoa());
		modelAndView.addObject("profissoes", profissaoRepository.findAll());
		return modelAndView;
	}

	@PostMapping(value = "**/pesquisapessoa")
	public ModelAndView pesquisar(@RequestParam("nomepesquisa") String nomepesquisa,
			@RequestParam("pesqsexo") String pesqsexo,
			@PageableDefault(size = 5, sort = { "nome" }) Pageable pageable) {
		Page<Pessoa> pessoas = null;
		// todos os campos tem dados
		if (pesqsexo != null && !pesqsexo.isEmpty() && nomepesquisa != null && !nomepesquisa.isEmpty()) {
			pessoas = pessoaRepository.findPessoaByNameSexoPage(nomepesquisa, pesqsexo, pageable);
		} else if (nomepesquisa != null && !nomepesquisa.isEmpty()) {
			pessoas = pessoaRepository.findPessoaByNamePage(nomepesquisa, pageable);
		} else if (pesqsexo != null && !pesqsexo.isEmpty()) {
			pessoas = pessoaRepository.findPessoaBySexoPage(pesqsexo, pageable);
		} else {
			pessoas = pessoaRepository.findPessoaByPage(nomepesquisa, pesqsexo, pageable);
		}
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		modelAndView.addObject("pessoas", pessoas);
		modelAndView.addObject("pessoaobj", new Pessoa());
		modelAndView.addObject("nomepesquisa", nomepesquisa);
		modelAndView.addObject("profissoes", profissaoRepository.findAll());
		return modelAndView;
	}

	@GetMapping("**/telefones/{idpessoa}")
	public ModelAndView telefones(@PathVariable("idpessoa") Long idpessoa) {
		Pessoa pessoa = pessoaRepository.findById(idpessoa).get();
		ModelAndView modelAndView = new ModelAndView("cadastro/telefones");
		modelAndView.addObject("telefones", telefoneRepository.getTelefones(idpessoa));
		modelAndView.addObject("pessoaobj", pessoa);
		modelAndView.addObject("foneobj", new Telefone());
		return modelAndView;
	}

	@PostMapping("**/addfonepessoa/{pessoaid}")
	public ModelAndView addFonePessoa(@Valid Telefone telefone, BindingResult bindingResult,
			@PathVariable("pessoaid") Long pessoaid) {
		if (bindingResult.hasErrors()) {
			ModelAndView andView = new ModelAndView("cadastro/telefones");
			Pessoa pessoa = pessoaRepository.findById(pessoaid).get();
			telefone.setPessoa(pessoa);
			andView.addObject("pessoaobj", pessoa);
			andView.addObject("foneobj", telefone);
			andView.addObject("telefones", telefoneRepository.getTelefones(pessoaid));
			List<String> msg = new ArrayList<String>();
			for (ObjectError objectError : bindingResult.getAllErrors()) {
				msg.add(objectError.getDefaultMessage()); // vem das anotações @NotEmpty e @NotNull e outras

			}
			andView.addObject("msg", msg);
			return andView;
		}

		ModelAndView modelAndView = new ModelAndView("cadastro/telefones");
		Pessoa pessoa = pessoaRepository.findById(pessoaid).get();
		telefone.setPessoa(pessoa);
		telefoneRepository.save(telefone);
		modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoaid));
		modelAndView.addObject("pessoaobj", pessoa);
		modelAndView.addObject("foneobj", new Telefone());
		return modelAndView;
	}

	@GetMapping("**/removertelefone/{idtelefone}")
	public ModelAndView removerTelefone(@PathVariable("idtelefone") Long idtelefone) {
		Pessoa pessoa = telefoneRepository.findById(idtelefone).get().getPessoa();
		telefoneRepository.deleteById(idtelefone);
		ModelAndView modelAndView = new ModelAndView("cadastro/telefones");
		modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoa.getId()));
		modelAndView.addObject("pessoaobj", pessoa);
		modelAndView.addObject("foneobj", new Telefone());
		return modelAndView;
	}

	@GetMapping("**/baixarcurriculo/{idpessoa}")
	public void baixarCurriculo(@PathVariable("idpessoa") Long idpessoa, HttpServletResponse response)
			throws IOException {
		// Consultar o objeto pessoa no banco de dados.
		Pessoa pessoa = pessoaRepository.findById(idpessoa).get();
		if (pessoa.getCurriculo() != null) {
			// setar tamanho da resposta
			response.setContentLength(pessoa.getCurriculo().length);
			// tipo do arquivo para download
			response.setContentType(pessoa.getTipoFileCurriculo());
			// define cabeçalho da resposta
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", pessoa.getNomeFileCurriculo());
			response.setHeader(headerKey, headerValue);
			response.getOutputStream().write(pessoa.getCurriculo());
		}
	}

	@GetMapping(value = "**/pesquisapessoa")
	public void imprimirPdf(@RequestParam("nomepesquisa") String nomepesquisa,
			@RequestParam("pesqsexo") String pesqsexo, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		List<Pessoa> pessoas = new ArrayList<Pessoa>();
		if (pesqsexo != null && !pesqsexo.isEmpty() && nomepesquisa != null && !nomepesquisa.isEmpty()) {
			pessoas = pessoaRepository.findPessoaByNameSexo(nomepesquisa, pesqsexo);
		} else if (pesqsexo != null && !pesqsexo.isEmpty()) {
			pessoas = pessoaRepository.findPessoaBySexo(pesqsexo);
		} else if (nomepesquisa != null && !nomepesquisa.isEmpty()) {
			pessoas = pessoaRepository.findPessoaByName(nomepesquisa);
		} else {
			Iterable<Pessoa> iterable = pessoaRepository.findAll();
			for (Pessoa pessoa : iterable) {
				pessoas.add(pessoa);
			}
		}
		// Chamar o serviço que faz a geração do relatório
		byte[] pdf = reportUtil.geraRelatorio(pessoas, "pessoa", request.getServletContext());
		// Tamanho da resposta
		response.setContentLength(pdf.length);
		// Definir na resposta o tipo de arquivo
		response.setContentType("application/octet-stream");
		// Definir o cabeçalho da resposta
		String headerKey = "Content-Disposition";
		String headervalue = String.format("attachment; filename=\"%s\"", "relatorio.pdf");
		response.setHeader(headerKey, headervalue);
		// finaliza a resposta pro navegador
		response.getOutputStream().write(pdf);
	}

	@GetMapping("/pessoaspag")
	public ModelAndView carregaPessoaPorPaginacao(@PageableDefault(size = 5, sort = { "nome" }) Pageable pageable,
			ModelAndView modelAndView, @RequestParam("nomepesquisa") String nomepesquisa) {
		Page<Pessoa> pagePessoa = pessoaRepository.findPessoaByNamePage(nomepesquisa, pageable);
		modelAndView.addObject("pessoas", pagePessoa);
		modelAndView.addObject("pessoaobj", new Pessoa());
		modelAndView.addObject("nomepesquisa", nomepesquisa);
		modelAndView.addObject("profissoes", profissaoRepository.findAll());
		modelAndView.setViewName("cadastro/cadastropessoa");
		return modelAndView;
	}

}
