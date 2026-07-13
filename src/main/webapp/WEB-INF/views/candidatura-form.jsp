<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Nova candidatura"/>
<c:set var="pageKey" value="form"/>
<%@ include file="fragments/header.jspf" %>

<section class="hero form-hero">
    <div class="hero-content">
        <span class="eyebrow">Junte-se ao nosso talento</span>
        <h1>A sua próxima conquista pode começar aqui.</h1>
        <p>Área pública do candidato. Submeta a sua candidatura sem precisar de criar conta ou fazer login.</p>
        <div class="hero-features">
            <span><b>01</b> Preenchimento guiado</span>
            <span><b>02</b> Dados protegidos</span>
            <span><b>03</b> Confirmação imediata</span>
        </div>
    </div>
    <div class="hero-decoration" aria-hidden="true"><span></span><span></span><span></span></div>
</section>

<div class="content-shell form-shell">
    <div class="form-intro">
        <div><span class="section-kicker">Formulário público de candidatura</span><h2>Conte-nos quem é.</h2></div>
        <p>Os campos marcados com <strong>*</strong> são obrigatórios. Reveja os dados antes de submeter.</p>
    </div>

    <c:if test="${not empty errors}">
        <div class="alert alert-error" role="alert" tabindex="-1" id="form-errors">
            <strong>Encontrámos <c:out value="${errors.size()}"/> campo(s) a corrigir.</strong>
            <p>Veja as mensagens assinaladas no formulário.</p>
        </div>
    </c:if>

    <form class="application-form" method="post"
          action="${pageContext.request.contextPath}/candidaturas/nova" data-application-form>
        <input type="hidden" name="csrfToken" value="<c:out value='${csrfToken}'/>">

        <section class="form-section" aria-labelledby="dados-title">
            <div class="form-section-heading"><span>01</span><div><small>Quem é</small><h2 id="dados-title">Dados pessoais</h2></div></div>
            <div class="form-grid">
                <div class="field field-full">
                    <label for="nomeCompleto">Nome completo <b>*</b></label>
                    <input id="nomeCompleto" name="nomeCompleto" type="text" required minlength="3" maxlength="100"
                           autocomplete="name" value="<c:out value='${candidatura.nomeCompleto}'/>"
                           aria-invalid="${not empty errors.nomeCompleto}">
                    <c:if test="${not empty errors.nomeCompleto}"><span class="field-error"><c:out value="${errors.nomeCompleto}"/></span></c:if>
                </div>
                <div class="field">
                    <label for="dataNascimento">Data de nascimento <b>*</b></label>
                    <input id="dataNascimento" name="dataNascimento" type="date" required max="${hoje}"
                           value="<c:out value='${candidatura.dataNascimento}'/>" aria-invalid="${not empty errors.dataNascimento}">
                    <c:if test="${not empty errors.dataNascimento}"><span class="field-error"><c:out value="${errors.dataNascimento}"/></span></c:if>
                </div>
                <fieldset class="field choice-field">
                    <legend>Sexo <b>*</b></legend>
                    <div class="inline-choices">
                        <label><input type="radio" name="sexo" value="Masculino" required <c:if test="${candidatura.sexo eq 'Masculino'}">checked</c:if>> Masculino</label>
                        <label><input type="radio" name="sexo" value="Feminino" <c:if test="${candidatura.sexo eq 'Feminino'}">checked</c:if>> Feminino</label>
                    </div>
                    <c:if test="${not empty errors.sexo}"><span class="field-error"><c:out value="${errors.sexo}"/></span></c:if>
                </fieldset>
                <div class="field">
                    <label for="nacionalidade">Nacionalidade <b>*</b></label>
                    <select id="nacionalidade" name="nacionalidade" required aria-invalid="${not empty errors.nacionalidade}">
                        <option value="">Seleccione</option>
                        <c:forEach items="${nacionalidades}" var="item"><option value="<c:out value='${item}'/>" <c:if test="${candidatura.nacionalidade eq item}">selected</c:if>><c:out value="${item}"/></option></c:forEach>
                    </select>
                    <c:if test="${not empty errors.nacionalidade}"><span class="field-error"><c:out value="${errors.nacionalidade}"/></span></c:if>
                </div>
                <div class="field">
                    <label for="biPassaporte">B.I. / Passaporte <b>*</b></label>
                    <input id="biPassaporte" name="biPassaporte" type="text" required minlength="6" maxlength="20"
                           pattern="[A-Za-z0-9/-]{6,20}" value="<c:out value='${candidatura.biPassaporte}'/>"
                           aria-invalid="${not empty errors.biPassaporte}">
                    <c:if test="${not empty errors.biPassaporte}"><span class="field-error"><c:out value="${errors.biPassaporte}"/></span></c:if>
                </div>
                <div class="field">
                    <label for="residenciaPais">País de residência <b>*</b></label>
                    <select id="residenciaPais" name="residenciaPais" required aria-invalid="${not empty errors.residenciaPais}">
                        <option value="">Seleccione</option>
                        <c:forEach items="${paises}" var="item"><option value="<c:out value='${item}'/>" <c:if test="${candidatura.residenciaPais eq item}">selected</c:if>><c:out value="${item}"/></option></c:forEach>
                    </select>
                    <c:if test="${not empty errors.residenciaPais}"><span class="field-error"><c:out value="${errors.residenciaPais}"/></span></c:if>
                </div>
                <div class="field">
                    <label for="provincia">Província / região <b>*</b></label>
                    <select id="provincia" name="provincia" required data-selected="<c:out value='${candidatura.provincia}'/>" aria-invalid="${not empty errors.provincia}">
                        <option value="">Escolha primeiro o país</option>
                    </select>
                    <c:if test="${not empty errors.provincia}"><span class="field-error"><c:out value="${errors.provincia}"/></span></c:if>
                </div>
                <div class="field">
                    <label for="email">E-mail <b>*</b></label>
                    <input id="email" name="email" type="email" required maxlength="254" autocomplete="email"
                           value="<c:out value='${candidatura.email}'/>" aria-invalid="${not empty errors.email}">
                    <c:if test="${not empty errors.email}"><span class="field-error"><c:out value="${errors.email}"/></span></c:if>
                </div>
                <div class="field">
                    <label for="contactoTelefonico">Contacto telefónico <b>*</b></label>
                    <div class="input-prefix"><span>+244</span><input id="contactoTelefonico" name="contactoTelefonico" type="tel" required inputmode="numeric" autocomplete="tel" pattern="9[0-9]{8}" maxlength="9" value="<c:out value='${candidatura.contactoTelefonico}'/>" aria-invalid="${not empty errors.contactoTelefonico}"></div>
                    <c:if test="${not empty errors.contactoTelefonico}"><span class="field-error"><c:out value="${errors.contactoTelefonico}"/></span></c:if>
                </div>
            </div>
        </section>

        <section class="form-section" aria-labelledby="formacao-title">
            <div class="form-section-heading"><span>02</span><div><small>O que aprendeu</small><h2 id="formacao-title">Habilitações académicas</h2></div></div>
            <div class="form-grid">
                <div class="field field-full">
                    <label for="nivelEscolaridade">Nível de escolaridade <b>*</b></label>
                    <select id="nivelEscolaridade" name="nivelEscolaridade" required aria-invalid="${not empty errors.nivelEscolaridade}">
                        <option value="">Seleccione</option>
                        <c:forEach items="${niveisEscolaridade}" var="item"><option value="<c:out value='${item}'/>" <c:if test="${candidatura.nivelEscolaridade eq item}">selected</c:if>><c:out value="${item}"/></option></c:forEach>
                    </select>
                    <c:if test="${not empty errors.nivelEscolaridade}"><span class="field-error"><c:out value="${errors.nivelEscolaridade}"/></span></c:if>
                </div>
                <fieldset class="field field-full checkbox-group">
                    <legend>Áreas de estudo <b>*</b> <small>Seleccione uma ou mais opções</small></legend>
                    <div class="checkbox-grid">
                        <c:forEach items="${areasEstudo}" var="area">
                            <label><input type="checkbox" name="areasEstudo" value="<c:out value='${area}'/>" <c:if test="${candidatura.areasEstudo.contains(area)}">checked</c:if> data-other-toggle="${area eq 'Outra' ? 'outraAreaEstudo' : ''}"><span><c:out value="${area}"/></span></label>
                        </c:forEach>
                    </div>
                    <c:if test="${not empty errors.areasEstudo}"><span class="field-error"><c:out value="${errors.areasEstudo}"/></span></c:if>
                </fieldset>
                <div class="field field-full optional-field" data-other-field="outraAreaEstudo">
                    <label for="outraAreaEstudo">Especifique a outra área <b>*</b></label>
                    <input id="outraAreaEstudo" name="outraAreaEstudo" type="text" maxlength="120" value="<c:out value='${candidatura.outraAreaEstudo}'/>" aria-invalid="${not empty errors.outraAreaEstudo}">
                    <c:if test="${not empty errors.outraAreaEstudo}"><span class="field-error"><c:out value="${errors.outraAreaEstudo}"/></span></c:if>
                </div>
                <div class="field">
                    <label for="cursoTecnico">Curso / curso técnico <b>*</b></label>
                    <input id="cursoTecnico" name="cursoTecnico" type="text" required maxlength="120" value="<c:out value='${candidatura.cursoTecnico}'/>" aria-invalid="${not empty errors.cursoTecnico}">
                    <c:if test="${not empty errors.cursoTecnico}"><span class="field-error"><c:out value="${errors.cursoTecnico}"/></span></c:if>
                </div>
                <div class="field">
                    <label for="instituicao">Instituição <b>*</b></label>
                    <input id="instituicao" name="instituicao" type="text" required maxlength="120" value="<c:out value='${candidatura.instituicao}'/>" aria-invalid="${not empty errors.instituicao}">
                    <c:if test="${not empty errors.instituicao}"><span class="field-error"><c:out value="${errors.instituicao}"/></span></c:if>
                </div>
                <div class="field">
                    <label for="paisFormacao">País da formação <b>*</b></label>
                    <select id="paisFormacao" name="paisFormacao" required aria-invalid="${not empty errors.paisFormacao}">
                        <option value="">Seleccione</option>
                        <c:forEach items="${paises}" var="item"><option value="<c:out value='${item}'/>" <c:if test="${candidatura.paisFormacao eq item}">selected</c:if>><c:out value="${item}"/></option></c:forEach>
                    </select>
                    <c:if test="${not empty errors.paisFormacao}"><span class="field-error"><c:out value="${errors.paisFormacao}"/></span></c:if>
                </div>
                <div class="field">
                    <label for="dataFimCurso">Data de conclusão <small>(opcional)</small></label>
                    <input id="dataFimCurso" name="dataFimCurso" type="date" max="${hoje}" value="<c:out value='${candidatura.dataFimCurso}'/>" aria-invalid="${not empty errors.dataFimCurso}">
                    <c:if test="${not empty errors.dataFimCurso}"><span class="field-error"><c:out value="${errors.dataFimCurso}"/></span></c:if>
                </div>
            </div>
        </section>

        <section class="form-section" aria-labelledby="interesses-title">
            <div class="form-section-heading"><span>03</span><div><small>Onde quer chegar</small><h2 id="interesses-title">Interesses profissionais</h2></div></div>
            <fieldset class="field field-full checkbox-group">
                <legend>Áreas de preferência <b>*</b> <small>Seleccione uma ou mais opções</small></legend>
                <div class="checkbox-grid">
                    <c:forEach items="${areasInteresse}" var="area">
                        <label><input type="checkbox" name="areasInteresse" value="<c:out value='${area}'/>" <c:if test="${candidatura.areasInteresse.contains(area)}">checked</c:if> data-other-toggle="${area eq 'Outro' ? 'outraAreaInteresse' : ''}"><span><c:out value="${area}"/></span></label>
                    </c:forEach>
                </div>
                <c:if test="${not empty errors.areasInteresse}"><span class="field-error"><c:out value="${errors.areasInteresse}"/></span></c:if>
            </fieldset>
            <div class="field field-full optional-field" data-other-field="outraAreaInteresse">
                <label for="outraAreaInteresse">Especifique a outra área <b>*</b></label>
                <input id="outraAreaInteresse" name="outraAreaInteresse" type="text" maxlength="120" value="<c:out value='${candidatura.outraAreaInteresse}'/>" aria-invalid="${not empty errors.outraAreaInteresse}">
                <c:if test="${not empty errors.outraAreaInteresse}"><span class="field-error"><c:out value="${errors.outraAreaInteresse}"/></span></c:if>
            </div>
        </section>

        <section class="form-section" aria-labelledby="perfil-title">
            <div class="form-section-heading"><span>04</span><div><small>O seu diferencial</small><h2 id="perfil-title">Perfil profissional</h2></div></div>
            <div class="form-grid">
                <div class="field field-full">
                    <label for="objectivosProfissionais">Objectivos profissionais <b>*</b></label>
                    <textarea id="objectivosProfissionais" name="objectivosProfissionais" required maxlength="2000" rows="5" aria-invalid="${not empty errors.objectivosProfissionais}" data-counter><c:out value="${candidatura.objectivosProfissionais}"/></textarea>
                    <span class="field-hint"><span data-count>0</span>/2000 caracteres</span>
                    <c:if test="${not empty errors.objectivosProfissionais}"><span class="field-error"><c:out value="${errors.objectivosProfissionais}"/></span></c:if>
                </div>
                <div class="field field-full">
                    <label for="resumoProfissional">Resumo profissional / Curriculum Vitae <b>*</b></label>
                    <textarea id="resumoProfissional" name="resumoProfissional" required maxlength="5000" rows="7" aria-invalid="${not empty errors.resumoProfissional}" data-counter><c:out value="${candidatura.resumoProfissional}"/></textarea>
                    <span class="field-hint"><span data-count>0</span>/5000 caracteres</span>
                    <c:if test="${not empty errors.resumoProfissional}"><span class="field-error"><c:out value="${errors.resumoProfissional}"/></span></c:if>
                </div>
                <div class="field">
                    <label for="linkedinUrl">URL do LinkedIn <small>(opcional)</small></label>
                    <input id="linkedinUrl" name="linkedinUrl" type="url" maxlength="500" placeholder="https://linkedin.com/in/seu-perfil" value="<c:out value='${candidatura.linkedinUrl}'/>" aria-invalid="${not empty errors.linkedinUrl}">
                    <c:if test="${not empty errors.linkedinUrl}"><span class="field-error"><c:out value="${errors.linkedinUrl}"/></span></c:if>
                </div>
                <div class="field">
                    <label for="portfolioUrl">URL do portfólio <small>(opcional)</small></label>
                    <input id="portfolioUrl" name="portfolioUrl" type="url" maxlength="500" placeholder="https://meuportfolio.com" value="<c:out value='${candidatura.portfolioUrl}'/>" aria-invalid="${not empty errors.portfolioUrl}">
                    <c:if test="${not empty errors.portfolioUrl}"><span class="field-error"><c:out value="${errors.portfolioUrl}"/></span></c:if>
                </div>
            </div>
        </section>

        <section class="form-section consent-section" aria-labelledby="consent-title">
            <div class="form-section-heading"><span>05</span><div><small>Confirmação</small><h2 id="consent-title">Termos e consentimento</h2></div></div>
            <label class="consent-box">
                <input type="checkbox" name="consentimentoAceite" required <c:if test="${candidatura.consentimentoAceite}">checked</c:if>>
                <span><strong>Autorizo o tratamento dos meus dados pessoais.</strong><small>Confirmo que os dados são verdadeiros e autorizo a sua utilização pelo Banco ATLANTICO para fins de recrutamento.</small></span>
            </label>
            <c:if test="${not empty errors.consentimentoAceite}"><span class="field-error"><c:out value="${errors.consentimentoAceite}"/></span></c:if>
            <div class="form-grid signature-grid">
                <div class="field">
                    <label for="assinaturaCandidato">Assinatura do candidato <b>*</b></label>
                    <input id="assinaturaCandidato" name="assinaturaCandidato" type="text" required maxlength="100" value="<c:out value='${candidatura.assinaturaCandidato}'/>" aria-invalid="${not empty errors.assinaturaCandidato}">
                    <c:if test="${not empty errors.assinaturaCandidato}"><span class="field-error"><c:out value="${errors.assinaturaCandidato}"/></span></c:if>
                </div>
                <div class="field">
                    <label for="dataAssinatura">Data <b>*</b></label>
                    <input id="dataAssinatura" name="dataAssinatura" type="date" required max="${hoje}" value="<c:out value='${candidatura.dataAssinatura}'/>" aria-invalid="${not empty errors.dataAssinatura}">
                    <c:if test="${not empty errors.dataAssinatura}"><span class="field-error"><c:out value="${errors.dataAssinatura}"/></span></c:if>
                </div>
            </div>
        </section>

        <div class="form-actions">
            <div><strong>Pronto para submeter?</strong><span>Confirme se os dados estão correctos.</span></div>
            <button class="button button-ghost" type="reset">Limpar</button>
            <button class="button button-large" type="submit" data-submit-button><span>Submeter candidatura</span><b aria-hidden="true">→</b></button>
        </div>
    </form>
</div>

<%@ include file="fragments/footer.jspf" %>
