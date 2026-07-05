(() => {
  "use strict";

  const provinces = {
    "Angola": ["Bengo", "Benguela", "Bie", "Cabinda", "Cuando Cubango", "Cuanza Norte", "Cuanza Sul", "Cunene", "Huambo", "Huila", "Luanda", "Lunda Norte", "Lunda Sul", "Malanje", "Moxico", "Namibe", "Uige", "Zaire"],
    "Brasil": ["Acre", "Alagoas", "Amapa", "Amazonas", "Bahia", "Ceara", "Distrito Federal", "Espirito Santo", "Goias", "Maranhao", "Mato Grosso", "Mato Grosso do Sul", "Minas Gerais", "Para", "Paraiba", "Parana", "Pernambuco", "Piaui", "Rio de Janeiro", "Rio Grande do Norte", "Rio Grande do Sul", "Rondonia", "Roraima", "Santa Catarina", "Sao Paulo", "Sergipe", "Tocantins"],
    "Cabo Verde": ["Boa Vista", "Brava", "Maio", "Mosteiros", "Paul", "Porto Novo", "Praia", "Ribeira Brava", "Ribeira Grande", "Sal", "Santa Catarina", "Santa Cruz", "Sao Filipe", "Sao Vicente", "Tarrafal"],
    "Mocambique": ["Cabo Delgado", "Gaza", "Inhambane", "Manica", "Maputo", "Maputo Cidade", "Nampula", "Niassa", "Sofala", "Tete", "Zambezia"],
    "Namibia": ["Erongo", "Hardap", "Karas", "Kavango East", "Kavango West", "Khomas", "Kunene", "Ohangwena", "Omaheke", "Omusati", "Oshana", "Oshikoto", "Otjozondjupa", "Zambezi"],
    "Portugal": ["Aveiro", "Beja", "Braga", "Braganca", "Castelo Branco", "Coimbra", "Evora", "Faro", "Guarda", "Leiria", "Lisboa", "Madeira", "Portalegre", "Porto", "Santarem", "Setubal", "Viana do Castelo", "Vila Real", "Viseu", "Acores"],
    "Sao Tome e Principe": ["Agua Grande", "Cantagalo", "Caue", "Lemba", "Lobata", "Me-Zochi", "Pague"],
    "Africa do Sul": ["Eastern Cape", "Free State", "Gauteng", "KwaZulu-Natal", "Limpopo", "Mpumalanga", "Northern Cape", "North West", "Western Cape"]
  };

  const country = document.querySelector("#residenciaPais");
  const province = document.querySelector("#provincia");

  function populateProvinces(keepSelection = true) {
    if (!country || !province) return;
    const selected = keepSelection ? province.dataset.selected : "";
    const values = provinces[country.value] || [];
    province.replaceChildren(new Option(values.length ? "Seleccione" : "Escolha primeiro o pais", ""));
    values.forEach(value => province.add(new Option(value, value, false, value === selected)));
    province.disabled = values.length === 0;
    province.dataset.selected = "";
  }

  if (country && province) {
    populateProvinces(true);
    country.addEventListener("change", () => populateProvinces(false));
  }

  document.querySelectorAll("[data-other-toggle]").forEach(toggle => {
    const targetName = toggle.dataset.otherToggle;
    if (!targetName) return;
    const wrapper = document.querySelector(`[data-other-field="${targetName}"]`);
    const input = document.querySelector(`#${targetName}`);
    const sync = () => {
      wrapper?.classList.toggle("is-visible", toggle.checked);
      if (input) {
        input.disabled = !toggle.checked;
        input.required = toggle.checked;
        if (!toggle.checked) input.value = "";
      }
    };
    toggle.addEventListener("change", sync);
    sync();
  });

  const phone = document.querySelector("#contactoTelefonico");
  phone?.addEventListener("input", () => {
    phone.value = phone.value.replace(/\D/g, "").slice(0, 9);
  });

  document.querySelectorAll("[data-counter]").forEach(textarea => {
    const count = textarea.parentElement.querySelector("[data-count]");
    const update = () => { if (count) count.textContent = textarea.value.length; };
    textarea.addEventListener("input", update);
    update();
  });

  const form = document.querySelector("[data-application-form]");
  const validateGroup = name => {
    const options = [...document.querySelectorAll(`input[name="${name}"]`)];
    if (!options.length) return true;
    const valid = options.some(option => option.checked);
    options[0].setCustomValidity(valid ? "" : "Seleccione pelo menos uma opcao.");
    return valid;
  };

  if (form) {
    ["areasEstudo", "areasInteresse"].forEach(name => {
      form.querySelectorAll(`input[name="${name}"]`).forEach(input =>
        input.addEventListener("change", () => validateGroup(name))
      );
    });

    form.addEventListener("submit", event => {
      validateGroup("areasEstudo");
      validateGroup("areasInteresse");
      if (!form.checkValidity()) {
        event.preventDefault();
        form.reportValidity();
        return;
      }
      const button = form.querySelector("[data-submit-button]");
      if (button) {
        button.disabled = true;
        button.querySelector("span").textContent = "A submeter...";
      }
    });

    form.addEventListener("reset", () => window.setTimeout(() => {
      populateProvinces(false);
      document.querySelectorAll("[data-other-toggle]").forEach(item =>
        item.dispatchEvent(new Event("change"))
      );
    }, 0));
  }

  const errorAlert = document.querySelector("#form-errors");
  if (errorAlert) {
    errorAlert.focus();
    errorAlert.scrollIntoView({ behavior: "smooth", block: "center" });
  }
})();
