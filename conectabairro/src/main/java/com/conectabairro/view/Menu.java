package com.conectabairro.view;

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
            System.out.println("      CONECTA BAIRRO       ");
            System.out.println("==============================");
            System.out.println("1. Cadastrar Usuário");
            System.out.println("2. Listar Usuários");
            System.out.println("3. Publicar Vaga");
            System.out.println("4. Ver Vagas Abertas");
            System.out.println("5. Solicitar Serviço (Contrato)");
            System.out.println("6. Avaliar Usuário");
            System.out.println("7. Fazer Denúncia");
            System.out.println("0. Sair");
            System.out.println("==============================");
            System.out.print("Escolha uma opção: ");

            String opcao = scanner.nextLine();

            switch (opcao) {
                case "1":
                    cadastrarUsuario(scanner);
                    break;
                case "2":
                    listarUsuarios();
                    break;
                case "3":
                    publicarVaga(scanner);
                    break;
                case "4":
                    listarVagasAbertas();
                    break;
                case "5":
                    solicitarServico(scanner);
                    break;
                case "6":
                    avaliarUsuario(scanner);
                    break;
                case "7":
                    denunciarUsuario(scanner);
                    break;
                case "0":
                    executando = false;
                    System.out.println("\nSaindo...");
                    break;
                default:
                    System.out.println("\nOpção inválida! Tente novamente.");
            }
        }

        scanner.close();
    }


    private void cadastrarUsuario(Scanner scanner) {
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
                default: System.out.println("❌ Opção inválida! Digite 1, 2 ou 3.\n");
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
                System.out.println("Usuário cadastrado com sucesso!");
            } else {
                System.out.println("Erro ao cadastrar (Status " + response.statusCode() + ").");
                System.out.println("Motivo: " + response.body());
            }

        } catch (Exception e) {
            System.out.println("Erro de conexão com o servidor. Verifique se a API está rodando.");
            System.out.println("Detalhe: " + e.getMessage());
        }
    }

    private void listarUsuarios() {
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
                    System.out.println("Nenhum usuário cadastrado.");
                } else {
                    System.out.println("--------------------------------------------------");
                    for (Usuario u : usuarios) {
                        System.out.printf("ID: %-3d | Nome: %-15s | Perfil: %-12s | E-mail: %s\n",
                                u.getId(), u.getNome(), u.getTipoPerfil(), u.getEmail());
                    }
                    System.out.println("--------------------------------------------------");
                }
            } else {
                System.out.println("Erro ao buscar usuários (Status " + response.statusCode() + ").");
            }

        } catch (Exception e) {
            System.out.println("Erro de conexão com o servidor. Verifique se a API está rodando.");
            System.out.println("Detalhe: " + e.getMessage());
        }
    }

    private void publicarVaga(Scanner scanner) {
        System.out.println("\n--- PUBLICAR NOVA VAGA ---");

        String idUsuario = selecionarUsuarioNaLista(scanner, "Autor da Vaga");

        System.out.print("Título da Vaga (ex: Encanador urgente): ");
        String titulo = scanner.nextLine();

        System.out.print("Descrição do serviço: ");
        String descricao = scanner.nextLine();

        System.out.print("Valor Estimado (ex: 150.50): ");
        String valor = scanner.nextLine().replace(",", ".");

        System.out.println("\n⏳ Enviando dados para o servidor...");

        String jsonBody = """
                {
                    "titulo": "%s",
                    "descricao": "%s",
                    "valorEstimado": %s,
                    "contratante": {
                        "id": %s
                    }
                }
                """.formatted(titulo, descricao, valor, idUsuario);

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/v1/vagas"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201) {
                System.out.println("Vaga publicada com sucesso!");
            } else {
                System.out.println("Erro ao publicar vaga (Status " + response.statusCode() + ").");
                System.out.println("Motivo: " + response.body());
            }

        } catch (Exception e) {
            System.out.println("Erro de conexão com o servidor. Verifique se a API está rodando.");
            System.out.println("Detalhe: " + e.getMessage());
        }
    }

    private void listarVagasAbertas() {
        System.out.println("\n--- VAGAS DISPONÍVEIS (ABERTAS) ---");

        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/v1/vagas/abertas"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String jsonBody = response.body();

                ObjectMapper mapper = new ObjectMapper();
                List<Vaga> vagas = mapper.readValue(jsonBody, new TypeReference<List<Vaga>>(){});

                if (vagas.isEmpty()) {
                    System.out.println("Nenhuma vaga aberta no momento. Tente novamente mais tarde!");
                } else {
                    System.out.println("-------------------------------------------------------------------------");
                    System.out.printf("%-5s | %-25s | %-10s | %-20s\n", "ID", "TÍTULO", "VALOR (R$)", "CONTRATANTE");
                    System.out.println("-------------------------------------------------------------------------");

                    for (Vaga v : vagas) {
                        String nomeContratante = v.getContratante() != null ? v.getContratante().getNome() : "Desconhecido";

                        System.out.printf("%-5d | %-25s | %-10.2f | %-20s\n",
                                v.getId(),
                                v.getTitulo().length() > 25 ? v.getTitulo().substring(0, 22) + "..." : v.getTitulo(),
                                v.getValorEstimado(),
                                nomeContratante);
                    }
                    System.out.println("-------------------------------------------------------------------------");
                }
            } else {
                System.out.println("Erro ao buscar vagas (Status " + response.statusCode() + ").");
            }

        } catch (Exception e) {
            System.out.println("Erro de conexão com o servidor. Verifique se a API está rodando.");
            System.out.println("Detalhe: " + e.getMessage());
        }
    }

    private void solicitarServico(Scanner scanner) {
        System.out.println("\n--- SOLICITAR SERVIÇO (NOVO CONTRATO) ---");

        String idConsumidor = selecionarUsuarioNaLista(scanner, "Quem é o Consumidor");

        String idPrestador = selecionarUsuarioNaLista(scanner, "Quem é o Prestador");

        System.out.print("Descreva o que você precisa que ele faça: ");
        String demanda = scanner.nextLine();

        System.out.println("\nEnviando solicitação para o servidor...");

        String jsonBody = """
                {
                    "consumidor": {
                        "id": %s
                    },
                    "prestador": {
                        "id": %s
                    },
                    "descricaoDemanda": "%s"
                }
                """.formatted(idConsumidor, idPrestador, demanda);

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/v1/contratos"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201) {
                System.out.println("Solicitação enviada com sucesso! O contrato agora está com status SOLICITADO.");
            } else {
                System.out.println("Erro ao solicitar serviço (Status " + response.statusCode() + ").");
                System.out.println("Motivo: " + response.body());
            }

        } catch (Exception e) {
            System.out.println("Erro de conexão com o servidor. Verifique se a API está rodando.");
            System.out.println("Detalhe: " + e.getMessage());
        }
    }

    private void avaliarUsuario(Scanner scanner) {
        System.out.println("\n--- AVALIAR USUÁRIO ---");

        String idAutor = selecionarUsuarioNaLista(scanner, "Autor da Avaliação");
        String idAvaliado = selecionarUsuarioNaLista(scanner, "Usuario avaliado");

        System.out.print("Nota (1 a 5 estrelas): ");
        String nota = scanner.nextLine();

        System.out.print("Deixe um comentário sobre o serviço/perfil: ");
        String comentario = scanner.nextLine();

        System.out.println("\nEnviando avaliação...");

        String jsonBody = """
                {
                    "autor": {
                        "id": %s
                    },
                    "avaliado": {
                        "id": %s
                    },
                    "nota": %s,
                    "comentario": "%s"
                }
                """.formatted(idAutor, idAvaliado, nota, comentario);

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/v1/avaliacoes"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201) {
                System.out.println("Avaliação registrada com sucesso!");
            } else {
                System.out.println("Erro ao avaliar (Status " + response.statusCode() + ").");
                System.out.println("Motivo: " + response.body());
            }
        } catch (Exception e) {
            System.out.println("Erro de conexão com o servidor. Detalhe: " + e.getMessage());
        }
    }

    private void denunciarUsuario(Scanner scanner) {
        System.out.println("\n--- REGISTRAR DENÚNCIA ---");

        String idUsuario = selecionarUsuarioNaLista(scanner, "Autor da Denuncia");

        System.out.print("Qual o motivo principal? (ex: Perfil Falso, Linguagem Ofensiva): ");
        String motivo = scanner.nextLine();

        System.out.print("Descreva os detalhes do ocorrido: ");
        String descricao = scanner.nextLine();

        System.out.println("\nEnviando denúncia para moderação...");

        String jsonBody = """
                {
                    "usuario": {
                        "id": %s
                    },
                    "motivo": "%s",
                    "descricao": "%s"
                }
                """.formatted(idUsuario, motivo, descricao);

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/v1/denuncias"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201) {
                System.out.println("Denúncia enviada com sucesso! Nossa equipe analisará o caso.");
            } else {
                System.out.println("Erro ao enviar denúncia (Status " + response.statusCode() + ").");
                System.out.println("Motivo: " + response.body());
            }
        } catch (Exception e) {
            System.out.println("Erro de conexão com o servidor. Detalhe: " + e.getMessage());
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
