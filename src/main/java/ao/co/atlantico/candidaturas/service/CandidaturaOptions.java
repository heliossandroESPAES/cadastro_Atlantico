package ao.co.atlantico.candidaturas.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class CandidaturaOptions {

    public static final List<String> NACIONALIDADES = List.of(
        "Angolana", "Brasileira", "Cabo-verdiana", "Guineense", "Mocambicana",
        "Namibiana", "Portuguesa", "Santomense", "Sul-africana"
    );

    public static final List<String> PAISES = List.of(
        "Angola", "Brasil", "Cabo Verde", "Mocambique", "Namibia", "Portugal",
        "Sao Tome e Principe", "Africa do Sul"
    );

    public static final List<String> NIVEIS_ESCOLARIDADE = List.of(
        "Ensino Medio", "Curso Tecnico", "Licenciatura", "Pos-Graduacao",
        "Mestrado", "Doutoramento"
    );

    public static final List<String> AREAS_ESTUDO = List.of(
        "Tecnologia e Engenharias", "Economia, Gestao, Contabilidade e Financas",
        "Ciencias e Saude", "Arquitectura e Artes", "Marketing, Comunicacao e Design",
        "Ciencias Juridicas", "Turismo", "Agricultura e Recursos Naturais",
        "Secretariado e Traducao", "Outra"
    );

    public static final List<String> AREAS_INTERESSE = List.of(
        "Banca Comercial", "Logistica", "Administrativa", "Direito", "Contabilidade",
        "Tecnologias", "Marketing", "Auditoria", "Gestao de Projectos",
        "Recursos Humanos", "Compliance", "Outro"
    );

    public static final Set<String> SEXOS = Set.of("Masculino", "Feminino");

    public static final Map<String, List<String>> PROVINCIAS_POR_PAIS = createProvinceMap();

    private CandidaturaOptions() {
    }

    public static boolean provinciaValida(String pais, String provincia) {
        return PROVINCIAS_POR_PAIS.getOrDefault(pais, List.of()).contains(provincia);
    }

    private static Map<String, List<String>> createProvinceMap() {
        Map<String, List<String>> values = new LinkedHashMap<>();
        values.put("Angola", List.of(
            "Bengo", "Benguela", "Bie", "Cabinda", "Cuando Cubango", "Cuanza Norte",
            "Cuanza Sul", "Cunene", "Huambo", "Huila", "Luanda", "Lunda Norte",
            "Lunda Sul", "Malanje", "Moxico", "Namibe", "Uige", "Zaire"
        ));
        values.put("Brasil", List.of(
            "Acre", "Alagoas", "Amapa", "Amazonas", "Bahia", "Ceara", "Distrito Federal",
            "Espirito Santo", "Goias", "Maranhao", "Mato Grosso", "Mato Grosso do Sul",
            "Minas Gerais", "Para", "Paraiba", "Parana", "Pernambuco", "Piaui",
            "Rio de Janeiro", "Rio Grande do Norte", "Rio Grande do Sul", "Rondonia",
            "Roraima", "Santa Catarina", "Sao Paulo", "Sergipe", "Tocantins"
        ));
        values.put("Cabo Verde", List.of(
            "Boa Vista", "Brava", "Maio", "Mosteiros", "Paul", "Porto Novo", "Praia",
            "Ribeira Brava", "Ribeira Grande", "Sal", "Santa Catarina", "Santa Cruz",
            "Sao Filipe", "Sao Vicente", "Tarrafal"
        ));
        values.put("Mocambique", List.of(
            "Cabo Delgado", "Gaza", "Inhambane", "Manica", "Maputo", "Maputo Cidade",
            "Nampula", "Niassa", "Sofala", "Tete", "Zambezia"
        ));
        values.put("Namibia", List.of(
            "Erongo", "Hardap", "Karas", "Kavango East", "Kavango West", "Khomas", "Kunene",
            "Ohangwena", "Omaheke", "Omusati", "Oshana", "Oshikoto", "Otjozondjupa", "Zambezi"
        ));
        values.put("Portugal", List.of(
            "Aveiro", "Beja", "Braga", "Braganca", "Castelo Branco", "Coimbra", "Evora",
            "Faro", "Guarda", "Leiria", "Lisboa", "Madeira", "Portalegre", "Porto",
            "Santarem", "Setubal", "Viana do Castelo", "Vila Real", "Viseu", "Acores"
        ));
        values.put("Sao Tome e Principe", List.of(
            "Agua Grande", "Cantagalo", "Caue", "Lemba", "Lobata", "Me-Zochi", "Pague"
        ));
        values.put("Africa do Sul", List.of(
            "Eastern Cape", "Free State", "Gauteng", "KwaZulu-Natal", "Limpopo",
            "Mpumalanga", "Northern Cape", "North West", "Western Cape"
        ));
        return Map.copyOf(values);
    }
}
