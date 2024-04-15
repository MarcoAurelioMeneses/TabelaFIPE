package br.com.marco.demo.principal;

import br.com.marco.demo.models.Dados;
import br.com.marco.demo.models.Modelos;
import br.com.marco.demo.models.Veiculo;
import br.com.marco.demo.services.ConsumoAPI;
import br.com.marco.demo.services.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private Scanner entradaDeDados = new Scanner(System.in);
    private ConsumoAPI consumo = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();
    private final String URL_BASE = "https://parallelum.com.br/fipe/api/v1/";


    public void exibeMenu(){
        var menu = """
                SELECIONE O VEICULO
                
                Carro
                Moto
                Caminhão
                
                Digite uma das opções para consultar:
                """;

        System.out.println(menu);
        var opcao = entradaDeDados.nextLine();

        String endereco;

        if(opcao.toLowerCase().contains("carr")){
            endereco = URL_BASE + "carros/marcas";
        } else if (opcao.toLowerCase().contains("mot")) {
            endereco = URL_BASE + "motos/marcas";
        }else {
            endereco = URL_BASE + "caminhoes/marcas";
        }

        var json = consumo.obterDados(endereco);
        System.out.println(json);
        var marcas = conversor.obterLista(json, Dados.class);
        marcas.stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("Informe o código da marca:");
        var codigoMarca = entradaDeDados.nextLine();

        endereco = endereco + "/" + codigoMarca + "/modelos";
        json = consumo.obterDados(endereco);
        var listaDeModelos = conversor.obterDados(json, Modelos.class);

        System.out.println("Modelos dessa marca: ");
        listaDeModelos.modelos().stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("Infome o carro que deseja pesquisar: ");
        var qualVeiculo = entradaDeDados.nextLine();
        List<Dados> filtroDeModelos = listaDeModelos.modelos().stream()
                .filter(m -> m.nome().toLowerCase().contains(qualVeiculo.toLowerCase()))
                .collect(Collectors.toList());

        System.out.println("Modelos encontrados: ");
        filtroDeModelos.forEach(System.out::println);

        System.out.println("Digite o código do modelo: ");
        var codigoModelo = entradaDeDados.nextLine();

        endereco = endereco + "/" + codigoModelo + "/anos";
        json = consumo.obterDados(endereco);

        List<Dados> anos = conversor.obterLista(json, Dados.class);
        List<Veiculo> veiculos = new ArrayList<>();

        for (int i = 0; i< anos.size(); i++){
            var enderecoAnos = endereco + "/" +  anos.get(i).codigo();
            json = consumo.obterDados(enderecoAnos);
            Veiculo veiculo = conversor.obterDados(json, Veiculo.class);
            veiculos.add(veiculo);
        }

        System.out.println("Veiculos encontrados: ");
        veiculos.forEach(System.out::println);


    }
}
