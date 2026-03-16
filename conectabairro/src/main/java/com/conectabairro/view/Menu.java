package com.conectabairro.view;

import com.conectabairro.model.Denuncia;
import com.conectabairro.model.Usuario;
import com.conectabairro.model.Vaga;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Scanner;

@Component
public class Menu implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        boolean executando = true;

        Thread.sleep(1000);

        while (executando) {
            System.out.println("\n==============================");
            System.out.println("      CONECTA BAIRRO        ");
            System.out.println("==============================");
            System.out.println("1. Menu de Usuários");
            System.out.println("2. Menu de Vagas");
            System.out.println("3. Menu de Contratos");
            System.out.println("4. Menu de Avaliações");
            System.out.println("5. Menu de Denúncias");
            System.out.println("6. Menu de Candidaturas");
            System.out.println("0. Sair do Sistema");
            System.out.println("==============================");
            System.out.print("Escolha um menu: ");

            String opcao = scanner.nextLine();

            switch (opcao) {
                 case "1": menuUsuarios(scanner); break;
                 case "2": menuVagas(scanner); break;
                 case "3": menuContratos(scanner); break;
                 case "4": menuAvaliacoes(scanner); break;
                 case "5": menuDenuncias(scanner); break;
                 case "6": menuCandidaturas(scanner); break;
                 case "0":
                    executando = false;
                    System.out.println("\nSaindo...");
                    break;
                 default:
                    System.out.println("\nOpção inválida!");
            }
        }
        scanner.close();
    }

    private void menuUsuarios(Scanner scanner) {
        boolean noMenu = true;
        while (noMenu) {
            System.out.println("\n--- MODULO DE USUARIOS ---");
            System.out.println("1. Cadastrar novo Usuario");
            System.out.println("2. Listar TODOS os Usuarios");
            System.out.println("3. Buscar Usuario por ID");
            System.out.println("4. Excluir Usuario");
            System.out.println("0. Voltar ao Menu Principal");
            System.out.print("Escolha: ");

            String opcao = scanner.nextLine();

            switch (opcao) {
                case "1":
                    System.out.println("\n--- NOVO CADASTRO ---");
                    System.out.print("Nome: ");
                    String nome = scanner.nextLine();
                    System.out.print("E-mail: ");
                    String email = scanner.nextLine();
                    System.out.print("Senha: ");
                    String senha = scanner.nextLine();
                    System.out.print("Telefone: ");
                    String telefone = scanner.nextLine();

                    String tipoPerfil = "";
                    while (tipoPerfil.isEmpty()) {
                        System.out.println("Tipo de Perfil:");
                        System.out.println("1 - CONTRATANTE");
                        System.out.println("2 - CANDIDATO");
                        System.out.println("3 - PRESTADOR");
                        System.out.print("Escolha (1-3): ");
                        String opcaoPerfil = scanner.nextLine();

                        switch (opcaoPerfil) {
                            case "1": tipoPerfil = "CONTRATANTE"; break;
                            case "2": tipoPerfil = "CANDIDATO"; break;
                            case "3": tipoPerfil = "PRESTADOR"; break;
                            default: System.out.println("Opcao invalida. Digite 1, 2 ou 3.\n");
                        }
                    }

                    String descricao = "";
                    if (tipoPerfil.equals("PRESTADOR")) {
                        System.out.print("Descreva suas habilidades: ");
                        descricao = scanner.nextLine();
                    }

                    String jsonBody = """
                            {
                                "nome": "%s",
                                "email": "%s",
                                "senha": "%s",
                                "telefone": "%s",
                                "tipoPerfil": "%s",
                                "descricaoHabilidades": "%s"
                            }
                            """.formatted(nome, email, senha, telefone, tipoPerfil, descricao);

                    try {
                        HttpClient client = HttpClient.newHttpClient();
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create("http://localhost:8080/v1/usuarios"))
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                                .build();

                        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                        if (response.statusCode() == 201) {
                            System.out.println("Usuario cadastrado com sucesso.");
                        } else {
                            System.out.println("Erro ao cadastrar (Status " + response.statusCode() + ").");
                            System.out.println("Motivo: " + response.body());
                        }
                    } catch (Exception e) {
                        System.out.println("Erro de conexao com o servidor: " + e.getMessage());
                    }
                    break;

                case "2":
                    System.out.println("\n--- LISTA DE USUARIOS ---");
                    try {
                        HttpClient client = HttpClient.newHttpClient();
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create("http://localhost:8080/v1/usuarios"))
                                .GET()
                                .build();

                        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                        if (response.statusCode() == 200) {
                            ObjectMapper mapper = new ObjectMapper();
                            List<Usuario> usuarios = mapper.readValue(response.body(), new TypeReference<List<Usuario>>(){});

                            if (usuarios.isEmpty()) {
                                System.out.println("Nenhum usuario cadastrado.");
                            } else {
                                System.out.println("--------------------------------------------------");
                                System.out.printf("%-5s | %-15s | %-12s | %s\n", "ID", "NOME", "PERFIL", "EMAIL");
                                System.out.println("--------------------------------------------------");
                                for (Usuario u : usuarios) {
                                    System.out.printf("%-5d | %-15s | %-12s | %s\n",
                                            u.getId(), u.getNome(), u.getTipoPerfil(), u.getEmail());
                                }
                                System.out.println("--------------------------------------------------");
                            }
                        } else {
                            System.out.println("Erro ao buscar usuarios (Status " + response.statusCode() + ").");
                        }
                    } catch (Exception e) {
                        System.out.println("Erro de conexao: " + e.getMessage());
                    }
                    break;

                case "3":
                    System.out.print("\nDigite o ID do Usuario que deseja buscar: ");
                    String idBusca = scanner.nextLine();
                    try {
                        HttpClient client = HttpClient.newHttpClient();
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create("http://localhost:8080/v1/usuarios/" + idBusca))
                                .GET()
                                .build();

                        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                        if (response.statusCode() == 200) {
                            ObjectMapper mapper = new ObjectMapper();
                            Usuario u = mapper.readValue(response.body(), Usuario.class);
                            System.out.println("\nUsuario Encontrado:");
                            System.out.println("ID: " + u.getId());
                            System.out.println("Nome: " + u.getNome());
                            System.out.println("E-mail: " + u.getEmail());
                            System.out.println("Telefone: " + u.getTelefone());
                            System.out.println("Perfil: " + u.getTipoPerfil());
                            if (u.getDescricaoHabilidades() != null && !u.getDescricaoHabilidades().isEmpty()) {
                                System.out.println("Habilidades: " + u.getDescricaoHabilidades());
                            }
                        } else {
                            System.out.println("Usuario nao encontrado (Status " + response.statusCode() + ").");
                        }
                    } catch (Exception e) {
                        System.out.println("Erro de conexao: " + e.getMessage());
                    }
                    break;

                case "4":
                    System.out.print("\nDigite o ID do Usuario que deseja EXCLUIR: ");
                    String idDelete = scanner.nextLine();
                    try {
                        HttpClient client = HttpClient.newHttpClient();
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create("http://localhost:8080/v1/usuarios/" + idDelete))
                                .DELETE()
                                .build();

                        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                        if (response.statusCode() == 204) {
                            System.out.println("Usuario excluido com sucesso.");
                        } else {
                            System.out.println("Erro ao excluir (Status " + response.statusCode() + ").");
                        }
                    } catch (Exception e) {
                        System.out.println("Erro de conexao: " + e.getMessage());
                    }
                    break;

                case "0":
                    noMenu = false;
                    break;
                default:
                    System.out.println("Opcao invalida.");
            }
        }
    }

    private void menuVagas(Scanner scanner) {
        boolean noMenu = true;
        while (noMenu) {
            System.out.println("\n--- MODULO DE VAGAS ---");
            System.out.println("1. Publicar nova Vaga");
            System.out.println("2. Listar TODAS as Vagas");
            System.out.println("3. Listar apenas Vagas ABERTAS");
            System.out.println("4. Buscar Vaga por ID");
            System.out.println("5. Fechar Vaga ");
            System.out.println("6. Excluir Vaga");
            System.out.println("0. Voltar ao Menu Principal");
            System.out.print("Escolha: ");

            String opcao = scanner.nextLine();

            switch (opcao) {
                case "1":
                    System.out.println("\n--- NOVA VAGA ---");
                    String idContratante = selecionarUsuarioNaLista(scanner, "Autor da Vaga");
                    if (idContratante.isEmpty()) break;

                    System.out.print("Titulo da Vaga: ");
                    String titulo = scanner.nextLine();
                    System.out.print("Descricao do servico: ");
                    String descricao = scanner.nextLine();
                    System.out.print("Valor Estimado (ex: 150.50): ");
                    String valor = scanner.nextLine().replace(",", ".");

                    String jsonBody = """
                            {
                                "titulo": "%s",
                                "descricao": "%s",
                                "valorEstimado": %s,
                                "contratante": {
                                    "id": %s
                                }
                            }
                            """.formatted(titulo, descricao, valor, idContratante);

                    enviarRequisicao("http://localhost:8080/v1/vagas", "POST", jsonBody);
                    break;

                case "2":
                    System.out.println("\n--- TODAS AS VAGAS ---");
                    listarVagasGenerico("http://localhost:8080/v1/vagas");
                    break;

                case "3":
                    System.out.println("\n--- VAGAS ABERTAS ---");
                    listarVagasGenerico("http://localhost:8080/v1/vagas/abertas");
                    break;

                case "4":
                    System.out.print("\nDigite o ID da Vaga que deseja buscar: ");
                    String idBusca = scanner.nextLine();
                    try {
                        HttpClient client = HttpClient.newHttpClient();
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create("http://localhost:8080/v1/vagas/" + idBusca))
                                .GET()
                                .build();

                        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                        if (response.statusCode() == 200) {
                            ObjectMapper mapper = new ObjectMapper();
                            Vaga v = mapper.readValue(response.body(), Vaga.class);
                            System.out.println("\nVaga Encontrada:");
                            System.out.println("ID: " + v.getId());
                            System.out.println("Titulo: " + v.getTitulo());
                            System.out.println("Descricao: " + v.getDescricao());
                            System.out.println("Valor: R$ " + v.getValorEstimado());
                            System.out.println("Status: " + v.getStatusVaga());
                            System.out.println("Contratante: " + (v.getContratante() != null ? v.getContratante().getNome() : "Desconhecido"));
                        } else {
                            System.out.println("Vaga nao encontrada (Status " + response.statusCode() + ").");
                        }
                    } catch (Exception e) {
                        System.out.println("Erro de conexao: " + e.getMessage());
                    }
                    break;

                case "5":
                    System.out.println("\n--- FECHAR VAGA ---");
                    System.out.print("Digite o ID da Vaga que deseja fechar: ");
                    String idVagaFechar = scanner.nextLine();
                    String idUsuarioFechar = selecionarUsuarioNaLista(scanner, "Autor da Vaga");
                    if (idUsuarioFechar.isEmpty()) break;

                    try {
                        HttpClient client = HttpClient.newHttpClient();
                        String url = "http://localhost:8080/v1/vagas/" + idVagaFechar + "/fechar?idUsuario=" + idUsuarioFechar;
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(url))
                                .PUT(HttpRequest.BodyPublishers.noBody()) // PUT sem corpo JSON
                                .build();

                        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                        if (response.statusCode() == 204 || response.statusCode() == 200) {
                            System.out.println("Vaga fechada com sucesso.");
                        } else {
                            System.out.println("Erro ao fechar (Status " + response.statusCode() + ").");
                        }
                    } catch (Exception e) {
                        System.out.println("Erro de conexao: " + e.getMessage());
                    }
                    break;

                case "6":
                    System.out.println("\n--- EXCLUIR VAGA ---");
                    System.out.print("Digite o ID da Vaga que deseja excluir: ");
                    String idVagaExcluir = scanner.nextLine();
                    String idUsuarioExcluir = selecionarUsuarioNaLista(scanner, "Autor da Vaga");
                    if (idUsuarioExcluir.isEmpty()) break;

                    try {
                        HttpClient client = HttpClient.newHttpClient();
                        String url = "http://localhost:8080/v1/vagas/" + idVagaExcluir + "?idUsuario=" + idUsuarioExcluir;
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(url))
                                .DELETE()
                                .build();

                        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                        if (response.statusCode() == 204) {
                            System.out.println("Vaga excluida com sucesso.");
                        } else {
                            System.out.println("Erro ao excluir (Status " + response.statusCode() + ").");
                        }
                    } catch (Exception e) {
                        System.out.println("Erro de conexao: " + e.getMessage());
                    }
                    break;

                case "0":
                    noMenu = false;
                    break;
                default:
                    System.out.println("Opcao invalida.");
            }
        }
    }

    private void menuContratos(Scanner scanner) {
        boolean noMenu = true;
        while (noMenu) {
            System.out.println("\n--- MODULO DE CONTRATOS (SERVICOS) ---");
            System.out.println("1. Solicitar Servico (POST)");
            System.out.println("2. Listar TODOS os Contratos (GET)");
            System.out.println("3. Buscar Contrato por ID (GET)");
            System.out.println("4. Orcar Servico - Visao do Prestador (PUT)");
            System.out.println("5. Aceitar Orcamento - Visao do Consumidor (PUT)");
            System.out.println("0. Voltar ao Menu Principal");
            System.out.print("Escolha: ");

            String opcao = scanner.nextLine();

            switch (opcao) {
                case "1":
                    System.out.println("\n--- SOLICITAR SERVICO ---");
                    String idConsumidor = selecionarUsuarioNaLista(scanner, "Qual o seu ID (Consumidor)?");
                    if (idConsumidor.isEmpty()) break;

                    String idPrestador = selecionarUsuarioNaLista(scanner, "Qual o ID do profissional (Prestador)?");
                    if (idPrestador.isEmpty()) break;

                    System.out.print("Descreva o que voce precisa: ");
                    String demanda = scanner.nextLine();

                    String jsonBody = """
                            {
                                "consumidor": { "id": %s },
                                "prestador": { "id": %s },
                                "descricaoDemanda": "%s"
                            }
                            """.formatted(idConsumidor, idPrestador, demanda);

                    enviarRequisicao("http://localhost:8080/v1/contratos", "POST", jsonBody);
                    break;

                case "2":
                    System.out.println("\n--- LISTA DE CONTRATOS ---");
                    try {
                        HttpClient client = HttpClient.newHttpClient();
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create("http://localhost:8080/v1/contratos"))
                                .GET()
                                .build();

                        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                        if (response.statusCode() == 200) {
                            ObjectMapper mapper = new ObjectMapper();
                            com.fasterxml.jackson.databind.JsonNode contratos = mapper.readTree(response.body());

                            if (contratos.isEmpty()) {
                                System.out.println("Nenhum contrato registrado.");
                            } else {
                                System.out.println("-------------------------------------------------------------------------------");
                                System.out.printf("%-5s | %-15s | %-15s | %-10s | %-15s\n", "ID", "CONSUMIDOR", "PRESTADOR", "VALOR", "STATUS");
                                System.out.println("-------------------------------------------------------------------------------");
                                for (com.fasterxml.jackson.databind.JsonNode c : contratos) {
                                    String nomeConsumidor = c.has("consumidor") && !c.get("consumidor").isNull() ? c.get("consumidor").get("nome").asText() : "N/A";
                                    String nomePrestador = c.has("prestador") && !c.get("prestador").isNull() ? c.get("prestador").get("nome").asText() : "N/A";
                                    String valor = c.has("valorAcordado") && !c.get("valorAcordado").isNull() ? "R$ " + c.get("valorAcordado").asText() : "Pendente";
                                    String status = c.has("status") ? c.get("status").asText() : "N/A";

                                    System.out.printf("%-5s | %-15s | %-15s | %-10s | %-15s\n",
                                            c.get("id").asText(), nomeConsumidor, nomePrestador, valor, status);
                                }
                                System.out.println("-------------------------------------------------------------------------------");
                            }
                        } else {
                            System.out.println("Erro ao listar (Status " + response.statusCode() + ").");
                        }
                    } catch (Exception e) {
                        System.out.println("Erro de conexao: " + e.getMessage());
                    }
                    break;

                case "3":
                    System.out.print("\nDigite o ID do Contrato que deseja buscar: ");
                    String idBusca = scanner.nextLine();
                    try {
                        HttpClient client = HttpClient.newHttpClient();
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create("http://localhost:8080/v1/contratos/" + idBusca))
                                .GET()
                                .build();

                        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                        if (response.statusCode() == 200) {
                            ObjectMapper mapper = new ObjectMapper();
                            com.fasterxml.jackson.databind.JsonNode c = mapper.readTree(response.body());

                            System.out.println("\nContrato Encontrado:");
                            System.out.println("ID: " + c.get("id").asText());
                            System.out.println("Consumidor: " + (c.has("consumidor") ? c.get("consumidor").get("nome").asText() : "N/A"));
                            System.out.println("Prestador: " + (c.has("prestador") ? c.get("prestador").get("nome").asText() : "N/A"));
                            System.out.println("Demanda: " + c.get("descricaoDemanda").asText());
                            System.out.println("Valor Acordado: R$ " + (c.has("valorAcordado") && !c.get("valorAcordado").isNull() ? c.get("valorAcordado").asText() : "Nao definido"));
                            System.out.println("Status: " + c.get("status").asText());
                        } else {
                            System.out.println("Contrato nao encontrado (Status " + response.statusCode() + ").");
                        }
                    } catch (Exception e) {
                        System.out.println("Erro de conexao: " + e.getMessage());
                    }
                    break;

                case "4":
                    System.out.println("\n--- ORCAR SERVICO (PRESTADOR) ---");
                    System.out.print("Digite o ID do Contrato que voce deseja orcar: ");
                    String idContratoOrcar = scanner.nextLine();
                    System.out.print("Qual o valor que voce vai cobrar? (ex: 200.00): ");
                    String valorOrcamento = scanner.nextLine().replace(",", ".");

                    String urlOrcar = "http://localhost:8080/v1/contratos/" + idContratoOrcar + "/orcar?valor=" + valorOrcamento;
                    enviarRequisicao(urlOrcar, "PUT", "");
                    break;

                case "5":
                    System.out.println("\n--- ACEITAR ORCAMENTO (CONSUMIDOR) ---");
                    System.out.print("Digite o ID do Contrato que voce deseja aceitar: ");
                    String idContratoAceitar = scanner.nextLine();

                    String urlAceitar = "http://localhost:8080/v1/contratos/" + idContratoAceitar + "/aceitar";
                    enviarRequisicao(urlAceitar, "PUT", "");
                    break;

                case "0":
                    noMenu = false;
                    break;
                default:
                    System.out.println("Opcao invalida.");
            }
        }
    }

    private void menuAvaliacoes(Scanner scanner) {
        boolean noMenu = true;
        while (noMenu) {
            System.out.println("\n--- MODULO DE AVALIACOES ---");
            System.out.println("1. Avaliar Usuario");
            System.out.println("2. Listar TODAS as Avaliacoes");
            System.out.println("3. Buscar Avaliacao por ID");
            System.out.println("4. Excluir Avaliacao");
            System.out.println("0. Voltar ao Menu Principal");
            System.out.print("Escolha: ");

            String opcao = scanner.nextLine();

            switch (opcao) {
                case "1":
                    System.out.println("\n--- NOVA AVALIACAO ---");
                    String idAutor = selecionarUsuarioNaLista(scanner, "Autor da avaliacao");
                    if (idAutor.isEmpty()) break;

                    String idAvaliado = selecionarUsuarioNaLista(scanner, "Usuario avaliado");
                    if (idAvaliado.isEmpty()) break;

                    System.out.print("Nota (1 a 5 estrelas): ");
                    String nota = scanner.nextLine();

                    System.out.print("Deixe um comentario sobre a experiencia: ");
                    String comentario = scanner.nextLine();

                    String jsonBody = """
                            {
                                "autor": { "id": %s },
                                "avaliado": { "id": %s },
                                "nota": %s,
                                "comentario": "%s"
                            }
                            """.formatted(idAutor, idAvaliado, nota, comentario);

                    enviarRequisicao("http://localhost:8080/v1/avaliacoes", "POST", jsonBody);
                    break;

                case "2":
                    System.out.println("\n--- LISTA DE AVALIACOES ---");
                    try {
                        HttpClient client = HttpClient.newHttpClient();
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create("http://localhost:8080/v1/avaliacoes"))
                                .GET()
                                .build();

                        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                        if (response.statusCode() == 200) {
                            ObjectMapper mapper = new ObjectMapper();
                            com.fasterxml.jackson.databind.JsonNode avaliacoes = mapper.readTree(response.body());

                            if (avaliacoes.isEmpty()) {
                                System.out.println("Nenhuma avaliacao registrada.");
                            } else {
                                System.out.println("-------------------------------------------------------------------------");
                                System.out.printf("%-5s | %-15s | %-15s | %-5s | %-20s\n", "ID", "AUTOR", "AVALIADO", "NOTA", "COMENTARIO");
                                System.out.println("-------------------------------------------------------------------------");
                                for (com.fasterxml.jackson.databind.JsonNode a : avaliacoes) {
                                    String nomeAutor = a.has("autor") && !a.get("autor").isNull() ? a.get("autor").get("nome").asText() : "N/A";
                                    String nomeAvaliado = a.has("avaliado") && !a.get("avaliado").isNull() ? a.get("avaliado").get("nome").asText() : "N/A";
                                    String notaAtual = a.has("nota") ? a.get("nota").asText() : "0";
                                    String com = a.has("comentario") ? a.get("comentario").asText() : "";

                                    String comCurto = com.length() > 20 ? com.substring(0, 17) + "..." : com;

                                    System.out.printf("%-5s | %-15s | %-15s | %-5s | %-20s\n",
                                            a.get("id").asText(), nomeAutor, nomeAvaliado, notaAtual, comCurto);
                                }
                                System.out.println("-------------------------------------------------------------------------");
                            }
                        } else {
                            System.out.println("Erro ao listar (Status " + response.statusCode() + ").");
                        }
                    } catch (Exception e) {
                        System.out.println("Erro de conexao: " + e.getMessage());
                    }
                    break;

                case "3":
                    System.out.print("\nDigite o ID da Avaliacao que deseja buscar: ");
                    String idBusca = scanner.nextLine();
                    try {
                        HttpClient client = HttpClient.newHttpClient();
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create("http://localhost:8080/v1/avaliacoes/" + idBusca))
                                .GET()
                                .build();

                        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                        if (response.statusCode() == 200) {
                            ObjectMapper mapper = new ObjectMapper();
                            com.fasterxml.jackson.databind.JsonNode a = mapper.readTree(response.body());

                            System.out.println("\nAvaliacao Encontrada:");
                            System.out.println("ID: " + a.get("id").asText());
                            System.out.println("Autor: " + (a.has("autor") ? a.get("autor").get("nome").asText() : "N/A"));
                            System.out.println("Avaliado: " + (a.has("avaliado") ? a.get("avaliado").get("nome").asText() : "N/A"));
                            System.out.println("Nota: " + a.get("nota").asText() + " estrelas");
                            System.out.println("Comentario: " + a.get("comentario").asText());
                        } else {
                            System.out.println("Avaliacao nao encontrada (Status " + response.statusCode() + ").");
                        }
                    } catch (Exception e) {
                        System.out.println("Erro de conexao: " + e.getMessage());
                    }
                    break;

                case "4":
                    System.out.print("\nDigite o ID da Avaliacao que deseja EXCLUIR: ");
                    String idDelete = scanner.nextLine();
                    try {
                        HttpClient client = HttpClient.newHttpClient();
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create("http://localhost:8080/v1/avaliacoes/" + idDelete))
                                .DELETE()
                                .build();

                        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                        if (response.statusCode() == 204) {
                            System.out.println("Avaliacao excluida com sucesso.");
                        } else {
                            System.out.println("Erro ao excluir (Status " + response.statusCode() + ").");
                        }
                    } catch (Exception e) {
                        System.out.println("Erro de conexao: " + e.getMessage());
                    }
                    break;

                case "0":
                    noMenu = false;
                    break;
                default:
                    System.out.println("Opcao invalida.");
            }
        }
    }

    private void menuDenuncias(Scanner scanner) {
        boolean noMenu = true;
        while (noMenu) {
            System.out.println("\n--- MÓDULO DE DENÚNCIAS ---");
            System.out.println("1. Registrar nova Denúncia ");
            System.out.println("2. Listar TODAS as Denúncias");
            System.out.println("3. Buscar Denúncia por ID");
            System.out.println("4. Excluir Denúncia");
            System.out.println("0. Voltar ao Menu Principal");
            System.out.print("Escolha: ");

            String opcao = scanner.nextLine();

            switch (opcao) {
                case "1":
                    System.out.println("\n--- NOVA DENÚNCIA ---");
                    String idUsuario = selecionarUsuarioNaLista(scanner, "Autor da Denuncia");
                    if (idUsuario.isEmpty()) break;

                    System.out.print("Motivo (ex: Perfil Falso): ");
                    String motivo = scanner.nextLine();
                    System.out.print("Descrição detalhada: ");
                    String descricao = scanner.nextLine();

                    String jsonBody = """
                            {
                                "usuario": { "id": %s },
                                "motivo": "%s",
                                "descricao": "%s"
                            }
                            """.formatted(idUsuario, motivo, descricao);
                    enviarRequisicao("http://localhost:8080/v1/denuncias", "POST", jsonBody);
                    break;

                case "2":
                    System.out.println("\n--- LISTA DE DENÚNCIAS ---");
                    try {
                        HttpClient client = HttpClient.newHttpClient();
                        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/v1/denuncias")).GET().build();
                        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                        if (response.statusCode() == 200) {
                            ObjectMapper mapper = new ObjectMapper();
                            List<Denuncia> denuncias = mapper.readValue(response.body(), new TypeReference<List<Denuncia>>(){});
                            if (denuncias.isEmpty()) {
                                System.out.println("Nenhuma denúncia registrada.");
                            } else {
                                for (Denuncia d : denuncias) {
                                    System.out.printf("ID: %d | Denunciante: %s | Motivo: %s\n", d.getId(), d.getUsuario().getNome(), d.getMotivo());
                                }
                            }
                        } else {
                            System.out.println("Erro ao listar (Status " + response.statusCode() + ")");
                        }
                    } catch (Exception e) {
                        System.out.println("Erro de conexão.");
                    }
                    break;

                case "3":
                    System.out.print("\nDigite o ID da Denúncia que deseja buscar: ");
                    String idBusca = scanner.nextLine();
                    try {
                        HttpClient client = HttpClient.newHttpClient();
                        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/v1/denuncias/" + idBusca)).GET().build();
                        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                        if (response.statusCode() == 200) {
                            ObjectMapper mapper = new ObjectMapper();
                            Denuncia d = mapper.readValue(response.body(), Denuncia.class);
                            System.out.println("\nDenúncia Encontrada:");
                            System.out.println("ID: " + d.getId());
                            System.out.println("Autor: " + d.getUsuario().getNome());
                            System.out.println("Motivo: " + d.getMotivo());
                            System.out.println("Descrição: " + d.getDescricao());
                        } else {
                            System.out.println("Denúncia não encontrada (Status " + response.statusCode() + ")");
                        }
                    } catch (Exception e) {
                        System.out.println("Erro de conexão.");
                    }
                    break;

                case "4":
                    System.out.print("\nDigite o ID da Denúncia que deseja EXCLUIR: ");
                    String idDelete = scanner.nextLine();
                    try {
                        HttpClient client = HttpClient.newHttpClient();
                        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/v1/denuncias/" + idDelete)).DELETE().build();
                        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                        if (response.statusCode() == 204) {
                            System.out.println("Denúncia excluída com sucesso!");
                        } else {
                            System.out.println("Erro ao excluir (Status " + response.statusCode() + ")");
                        }
                    } catch (Exception e) {
                        System.out.println("Erro de conexão.");
                    }
                    break;

                case "0":
                    noMenu = false;
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    private void menuCandidaturas(Scanner scanner) {
        boolean noMenu = true;
        while (noMenu) {
            System.out.println("\n--- MODULO DE CANDIDATURAS ---");
            System.out.println("1. Candidatar-se a uma Vaga");
            System.out.println("2. Listar TODAS as Candidaturas");
            System.out.println("3. Buscar Candidatura por ID");
            System.out.println("4. Avaliar Candidato - Visao do Contratante");
            System.out.println("0. Voltar ao Menu Principal");
            System.out.print("Escolha: ");

            String opcao = scanner.nextLine();

            switch (opcao) {
                case "1":
                    System.out.println("\n--- NOVA CANDIDATURA ---");
                    String idCandidato = selecionarUsuarioNaLista(scanner, "Candidato");
                    if (idCandidato.isEmpty()) break;

                    System.out.print("Digite o ID da Vaga que deseja se candidatar: ");
                    String idVaga = scanner.nextLine();

                    String jsonBody = """
                            {
                                "vaga": { "id": %s },
                                "candidato": { "id": %s }
                            }
                            """.formatted(idVaga, idCandidato);

                    enviarRequisicao("http://localhost:8080/v1/candidaturas", "POST", jsonBody);
                    break;

                case "2":
                    System.out.println("\n--- LISTA DE CANDIDATURAS ---");
                    try {
                        HttpClient client = HttpClient.newHttpClient();
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create("http://localhost:8080/v1/candidaturas"))
                                .GET()
                                .build();

                        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                        if (response.statusCode() == 200) {
                            ObjectMapper mapper = new ObjectMapper();
                            com.fasterxml.jackson.databind.JsonNode candidaturas = mapper.readTree(response.body());

                            if (candidaturas.isEmpty()) {
                                System.out.println("Nenhuma candidatura registrada.");
                            } else {
                                System.out.println("-------------------------------------------------------------------------------");
                                System.out.printf("%-5s | %-20s | %-20s | %-15s\n", "ID", "VAGA", "CANDIDATO", "STATUS");
                                System.out.println("-------------------------------------------------------------------------------");
                                for (com.fasterxml.jackson.databind.JsonNode c : candidaturas) {
                                    String tituloVaga = c.has("vaga") && !c.get("vaga").isNull() ? c.get("vaga").get("titulo").asText() : "N/A";
                                    String nomeCandidato = c.has("candidato") && !c.get("candidato").isNull() ? c.get("candidato").get("nome").asText() : "N/A";
                                    String status = c.has("statusCandidatura") ? c.get("statusCandidatura").asText() : "N/A";

                                    String tituloCurto = tituloVaga.length() > 20 ? tituloVaga.substring(0, 17) + "..." : tituloVaga;

                                    System.out.printf("%-5s | %-20s | %-20s | %-15s\n",
                                            c.get("id").asText(), tituloCurto, nomeCandidato, status);
                                }
                                System.out.println("-------------------------------------------------------------------------------");
                            }
                        } else {
                            System.out.println("Erro ao listar (Status " + response.statusCode() + ").");
                        }
                    } catch (Exception e) {
                        System.out.println("Erro de conexao: " + e.getMessage());
                    }
                    break;

                case "3":
                    System.out.print("\nDigite o ID da Candidatura que deseja buscar: ");
                    String idBusca = scanner.nextLine();
                    try {
                        HttpClient client = HttpClient.newHttpClient();
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create("http://localhost:8080/v1/candidaturas/" + idBusca))
                                .GET()
                                .build();

                        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                        if (response.statusCode() == 200) {
                            ObjectMapper mapper = new ObjectMapper();
                            com.fasterxml.jackson.databind.JsonNode c = mapper.readTree(response.body());

                            System.out.println("\nCandidatura Encontrada:");
                            System.out.println("ID: " + c.get("id").asText());
                            System.out.println("Vaga: " + (c.has("vaga") ? c.get("vaga").get("titulo").asText() : "N/A"));
                            System.out.println("Candidato: " + (c.has("candidato") ? c.get("candidato").get("nome").asText() : "N/A"));
                            System.out.println("Data: " + (c.has("dataCandidatura") ? c.get("dataCandidatura").asText() : "N/A"));
                            System.out.println("Status: " + c.get("statusCandidatura").asText());
                        } else {
                            System.out.println("Candidatura nao encontrada (Status " + response.statusCode() + ").");
                        }
                    } catch (Exception e) {
                        System.out.println("Erro de conexao: " + e.getMessage());
                    }
                    break;

                case "4":
                    System.out.println("\n--- AVALIAR CANDIDATO ---");
                    System.out.print("Digite o ID da Candidatura que deseja avaliar: ");
                    String idCandidatura = scanner.nextLine();

                    String idDonoVaga = selecionarUsuarioNaLista(scanner, "Autor da Vaga");
                    if (idDonoVaga.isEmpty()) break;

                    String novoStatus = "";
                    while (novoStatus.isEmpty()) {
                        System.out.println("Qual sera a decisao?");
                        System.out.println("1 - ACEITAR");
                        System.out.println("2 - REJEITAR");
                        System.out.print("Escolha (1-2): ");
                        String escolhaStatus = scanner.nextLine();
                        if (escolhaStatus.equals("1")) novoStatus = "ACEITA";
                        else if (escolhaStatus.equals("2")) novoStatus = "REJEITADA";
                        else System.out.println("Opcao invalida.");
                    }

                    String urlAvaliar = "http://localhost:8080/v1/candidaturas/" + idCandidatura + "/avaliar?idDonoVaga=" + idDonoVaga + "&status=" + novoStatus;
                    enviarRequisicao(urlAvaliar, "PUT", "");
                    break;

                case "0":
                    noMenu = false;
                    break;
                default:
                    System.out.println("Opcao invalida.");
            }
        }
    }

    private void enviarRequisicao(String url, String metodo, String jsonBody) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json");

            if (metodo.equals("POST")) {
                builder.POST(HttpRequest.BodyPublishers.ofString(jsonBody));
            } else if (metodo.equals("PUT")) {
                if (jsonBody == null || jsonBody.isEmpty()) {
                    builder.PUT(HttpRequest.BodyPublishers.noBody());
                } else {
                    builder.PUT(HttpRequest.BodyPublishers.ofString(jsonBody));
                }
            } else if (metodo.equals("DELETE")) {
                builder.DELETE();
            }

            HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201 || response.statusCode() == 200 || response.statusCode() == 204) {
                System.out.println("Operacao realizada com sucesso!");
            } else {
                System.out.println("Erro (Status " + response.statusCode() + "): " + response.body());
            }
        } catch (Exception e) {
            System.out.println("Erro de rede: " + e.getMessage());
        }
    }

    private void listarVagasGenerico(String url) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                List<Vaga> vagas = mapper.readValue(response.body(), new TypeReference<List<Vaga>>(){});

                if (vagas.isEmpty()) {
                    System.out.println("Nenhuma vaga encontrada.");
                } else {
                    System.out.println("-------------------------------------------------------------------------");
                    System.out.printf("%-5s | %-20s | %-10s | %-10s | %-15s\n", "ID", "TITULO", "VALOR", "STATUS", "CONTRATANTE");
                    System.out.println("-------------------------------------------------------------------------");
                    for (Vaga v : vagas) {
                        String nomeContratante = v.getContratante() != null ? v.getContratante().getNome() : "Desconhecido";
                        String tituloCurto = v.getTitulo().length() > 20 ? v.getTitulo().substring(0, 17) + "..." : v.getTitulo();

                        System.out.printf("%-5d | %-20s | %-10.2f | %-10s | %-15s\n",
                                v.getId(), tituloCurto, v.getValorEstimado(), v.getStatusVaga(), nomeContratante);
                    }
                    System.out.println("-------------------------------------------------------------------------");
                }
            } else {
                System.out.println("Erro ao listar vagas (Status " + response.statusCode() + ").");
            }
        } catch (Exception e) {
            System.out.println("Erro de conexao: " + e.getMessage());
        }
    }

    private String selecionarUsuarioNaLista(Scanner scanner, String pergunta) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/v1/usuarios"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String jsonBody = response.body();
                ObjectMapper mapper = new ObjectMapper();
                List<Usuario> usuarios = mapper.readValue(jsonBody, new TypeReference<List<Usuario>>(){});

                if (usuarios.isEmpty()) {
                    System.out.println("Nenhum usuário cadastrado no sistema.");
                    return "";
                }

                System.out.println("\n--- LISTA DE USUÁRIOS ---");
                System.out.println("--------------------------------------------------");
                System.out.printf("%-5s | %-15s | %-15s\n", "ID", "NOME", "PERFIL");
                System.out.println("--------------------------------------------------");
                for (Usuario u : usuarios) {
                    System.out.printf("%-5d | %-15s | %-15s\n", u.getId(), u.getNome(), u.getTipoPerfil());
                }
                System.out.println("--------------------------------------------------");

                System.out.print("\n" + pergunta + " (Digite o ID): ");
                return scanner.nextLine();

            } else {
                System.out.println("Erro ao buscar usuários (Status " + response.statusCode() + ").");
            }
        } catch (Exception e) {
            System.out.println("Erro de conexão com o servidor. Detalhe: " + e.getMessage());
        }

        return "";
    }
}
