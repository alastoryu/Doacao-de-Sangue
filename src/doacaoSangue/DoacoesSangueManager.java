package doacaoSangue;

import java.io.IOException;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

/**
 * Programa para manipulação de doações de sangue em um arquivo CSV.
 * 
 * @author Christopher Nitta
 * @since 05/12/2023
 */
public class DoacoesSangueManager {

	private static String ARQUIVO_CSV = "";

	public static Scanner scanner = new Scanner(System.in);

	/**
	 * Método principal que inicia o sistema de cadastro de Doação de Sangue.
	 *
	 * @param args Argumentos de linha de comando (não utilizados).
	 * @throws InterruptedException Se a execução for interrompida inesperadamente.
	 * @throws IOException          Se ocorrer um erro de E/S durante a execução.
	 */
	public static void main(String[] args) throws InterruptedException, IOException {
		System.out.println("\nSistema de cadastro de Doação de Sangue");
		caminhoCSV();
	}

	/**
	 * Solicita ao usuário que indique o caminho do arquivo CSV e verifica se o
	 * arquivo existe. Se existir, define o caminho do arquivo e exibe o menu
	 * principal.
	 *
	 * @throws InterruptedException Se a execução for interrompida inesperadamente.
	 * @throws IOException          Se ocorrer um erro de E/S durante a execução.
	 */
	private static void caminhoCSV() throws InterruptedException, IOException {
		System.out.println("\nIndique o caminho do arquivo CSV:");
		String csv = "";

		while (csv.isEmpty()) {
			try {
				csv = scanner.nextLine();
				ARQUIVO_CSV = csv;

				// Verifica se o arquivo existe
				if (!Files.exists(Paths.get(ARQUIVO_CSV))) {
					System.out.println("Arquivo não encontrado. Insira um caminho válido.");
					csv = "";
				}
			} catch (Exception e) {
				System.out.println("Erro ao ler o caminho do arquivo: " + e.getMessage() + " tente novamente.");
				csv = "";
			}
		}
		if (ARQUIVO_CSV != "") {
			System.out.println("O arquivo foi encontrado!");
			exibirMenu();
		}
	}

	/**
	 * Exibe o menu de opções para o usuário.
	 * 
	 * @throws InterruptedException
	 * @throws IOException
	 */
	private static void exibirMenu() throws InterruptedException, IOException {
		while (ARQUIVO_CSV != "") {
			System.out.println("\nMenu:");
			System.out.println("1. Exibir conteúdo do arquivo");
			System.out.println("2. Inserir nova doação");
			System.out.println("3. Deletar doação por código");
			System.out.println("4. Sair");
			try {
				System.out.print("Escolha a opção desejada: \n");
				int opcao = scanner.nextInt();

				switch (opcao) {
				case 1:
					exibirConteudoArquivo();
					break;
				case 2:
					inserirNovaDoacao();
					break;
				case 3:
					deletarDoacaoPorCodigo();
					break;
				case 4:
					System.out.println("Encerrando aplicação.");
					System.exit(0);
				default:
					System.out.println("Opção inválida. Tente novamente.");
				}
			} catch (java.util.InputMismatchException e) {
				System.out.println("Entrada inválida. Por favor, insira um dos número.");
				scanner.nextLine(); // Limpar o buffer do scanner para evitar loop infinito
			}
		}
	}

	/**
	 * Exibe o conteúdo do arquivo CSV.
	 * 
	 * @throws InterruptedException
	 * @throws IOException
	 */
	private static void exibirConteudoArquivo() throws InterruptedException, IOException {
		try {
			List<String> linhas = Files.readAllLines(Paths.get(ARQUIVO_CSV), StandardCharsets.UTF_8);
			for (String linha : linhas) {
				System.out.println(linha);
			}
		} catch (IOException e) {
			System.out.println("Erro ao ler o arquivo: " + e.getMessage());
			caminhoCSV();
		}
	}

	/**
	 * Insere uma nova doação no final do arquivo CSV.
	 * 
	 * @throws InterruptedException
	 */
	private static void inserirNovaDoacao() throws InterruptedException {
		try {
			// Solicita informações da nova doação ao usuário
			// Lê o último código presente no arquivo
			int ultimoCodigo = obterUltimoCodigo();

			// Gera o próximo código
			int novoCodigo = ultimoCodigo + 1;

			// Solicita informações da nova doação ao usuário
			System.out.println("Código automático gerado: " + novoCodigo);

			System.out.println("Nome: ");
			String nome = scanner.next();

			System.out.println("CPF: ");
			String cpf = scanner.next();

			System.out.println("Data de Nascimento (AAAA-MM-DD): ");
			String dataNascimento = scanner.next();

			System.out.println("Tipo Sanguíneo: ");
			String tipoSanguineo = scanner.next();

			System.out.println("MLs doados: ");
			int mlsDoados = scanner.nextInt();

			// Monta a nova linha a ser adicionada
			String novaLinha = String.format("%n%d,%s,%s,%s,%s,%d", novoCodigo, nome, cpf, dataNascimento,
					tipoSanguineo, mlsDoados);

			// Adiciona a nova linha ao final do arquivo
			Files.write(Paths.get(ARQUIVO_CSV), novaLinha.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
			System.out.println("\nDoação adicionada com sucesso!");
		} catch (IOException | java.util.InputMismatchException e) {
			System.out.println("Erro ao inserir a doação: " + e.getMessage());
		}
	}

	/**
	 * Obtém o último código presente no arquivo CSV.
	 *
	 * @return O último código.
	 * @throws IOException Em caso de erro na leitura do arquivo.
	 */
	private static int obterUltimoCodigo() throws IOException {
		List<String> linhas = Files.readAllLines(Paths.get(ARQUIVO_CSV), StandardCharsets.UTF_8);

		// Se o arquivo estiver vazio, retorna 0 como último código
		if (linhas.isEmpty()) {
			return 0;
		}

		// Obtém o último código do último registro no arquivo
		String ultimaLinha = linhas.get(linhas.size() - 1);
		String[] campos = ultimaLinha.split(",");
		return Integer.parseInt(campos[0]);
	}

	/**
	 * Deleta uma doação do arquivo CSV com base no código especificado.
	 */
	private static void deletarDoacaoPorCodigo() {
		try {
			System.out.print("Digite o código da doação que deseja excluir: ");
			int codigoParaExcluir = scanner.nextInt();
			scanner.nextLine(); // Consumir a quebra de linha pendente

			List<String> linhas = Files.readAllLines(Paths.get(ARQUIVO_CSV), StandardCharsets.UTF_8);

			// Encontrar e remover a linha com o código especificado
			for (int i = 0; i < linhas.size(); i++) {
				String linha = linhas.get(i);
				String[] campos = linha.split(",");
				int codigoAtual = Integer.parseInt(campos[0]);

				if (codigoAtual == codigoParaExcluir) {
					// Remover a linha se encontrada
					linhas.remove(i);
					System.out.println("Doação removida com sucesso!");
					Files.write(Paths.get(ARQUIVO_CSV), linhas, StandardCharsets.UTF_8);
					return; // Saia do método depois de encontrar e excluir a doação
				}
			}
			// Se o código não foi encontrado
			System.out.println("Doação com o código " + codigoParaExcluir + " não encontrada.");

		} catch (IOException | java.util.InputMismatchException e) {
			System.out.println("Erro ao deletar a doação: " + e.getMessage());
		}
	}
}