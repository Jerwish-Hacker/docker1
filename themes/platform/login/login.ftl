<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('username','password')
displayInfo=realm.password && realm.registrationAllowed && !registrationDisabled??; section>
    <#if section="header">
        ${msg("loginAccountTitle")}
    <#elseif section="form">
        <div id="kc-form" class="${properties.kcMain!}">
            <div class="${properties.kcLoginContainer!}">
                <div class="${properties.kcLoginCard!}">
                    <div class="${properties.kcContainerWrapper!}">
                        <div class="${properties.kcLeftItem!}">
                            <img src="${properties.kcLoginCardCoverImg!}" alt="login" class="${properties.kcLoginMainImg!}">
                        </div>
                        <div class="${properties.kcRightItem!}">
                            <div class="${properties.kcFormBody!}">
                                <div class="${properties.kcBrandWrapper}">
                                    <img src="${properties.kcBrandPartner}" alt="logo"
                                         class="${properties.kcLogo!}">
                                    <img src="${properties.kcBrandPartner}" alt="aws_logo"
                                         class="${properties.kcAwsLogo}">
                                </div>
                                <p class="${properties.kcDescription!}">Sign into your account</p>
                                <div id="kc-form-wrapper">
                                    <#if realm.password>
                                        <form id="kc-form-login" onsubmit="login.disabled = true; return true;"
                                              action="${url.loginAction}" method="post"
                                        >
                                            <div class="${properties.kcFormGroupClass!}">
                                                <#if usernameEditDisabled??>
                                                    <input tabindex="1" id="username"
                                                           class="${properties.kcInputClass!}" name="username"
                                                           value="${(login.username!'')}"
                                                           placeholder="Email Address"
                                                           type="text" disabled />
                                                <#else>
                                                    <input tabindex="1"
                                                           placeholder="Password"
                                                           id="username"
                                                           class="${properties.kcInputClass!}" name="username"
                                                           value="${(login.username!'')}" type="text" autofocus
                                                           autocomplete="off"
                                                           aria-invalid="<#if messagesPerField.existsError('username','password')>true</#if>" />
                                                    <#if messagesPerField.existsError('username','password')>
                                                        <span id="input-error"
                                                              class="${properties.kcInputErrorMessageClass!}"
                                                              aria-live="polite">
                                                                    ${kcSanitize(messagesPerField.getFirstError('username','password'))?no_esc}
                                                                </span>
                                                    </#if>
                                                </#if>
                                            </div>
                                            <div class="${properties.kcFormGroupClass!}">
                                                <input tabindex="2" id="password"
                                                       class="${properties.kcInputClass!}" name="password"
                                                       type="password" autocomplete="off"
                                                       aria-invalid="<#if messagesPerField.existsError('username','password')>true</#if>" />
                                            </div>
                                            <input tabindex="4" class="btn btn-block login-btn mb-4" name="login" id="" type="submit" value="Sign In">
                                        </form>
                                    </#if>
                                    <div >
                                        <#if realm.resetPasswordAllowed>
                                            <span><a class="${properties.kcFormOptionsWrapperClass!}" tabindex="5" href="${url.loginResetCredentialsUrl}">${msg("doForgotPassword")}</a></span>
                                        </#if>
                                    </div>
                                    <nav class="${properties.kcLoginFooterNav}">
                                        <p>Empowering Education:</p>
                                        <p>Streamlining Schools with Innovative Technology</p>
                                    </nav>
                                </div>
                            </div>
                            <!-- rest thing  -->

                            <div id="kc-form-buttons" >
                                <#if
                                auth.selectedCredential?has_content>
                                    value="${auth.selectedCredential}"
                                </#if>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        </div>
        <#if realm.password && social.providers??>
            <div id="kc-social-providers" class="${properties.kcFormSocialAccountSectionClass!}">
                <hr />
                <h4>${msg("identity-provider-login-label")}</h4>

                <ul
                        class="${properties.kcFormSocialAccountListClass!} <#if social.providers?size gt 3>${properties.kcFormSocialAccountListGridClass!}</#if>">
                    <#list social.providers as p>
                        <a id="social-${p.alias}"
                           class="${properties.kcFormSocialAccountListButtonClass!} <#if social.providers?size gt 3>${properties.kcFormSocialAccountGridItem!}</#if>"
                           type="button" href="${p.loginUrl}">
                            <#if p.iconClasses?has_content>
                                <i class="${properties.kcCommonLogoIdP!} ${p.iconClasses!}" aria-hidden="true"></i>
                                <span
                                        class="${properties.kcFormSocialAccountNameClass!} kc-social-icon-text">${p.displayName!}</span>
                            <#else>
                                <span class="${properties.kcFormSocialAccountNameClass!}">${p.displayName!}</span>
                            </#if>
                        </a>
                    </#list>
                </ul>
            </div>
        </#if>
        </div>
    <#elseif section="info">
        <#if realm.password && realm.registrationAllowed && !registrationDisabled??>
            <div id="kc-registration-container">
                <div id="kc-registration">
                        <span>${msg("noAccount")} <a tabindex="6"
                                                     href="${url.registrationUrl}">${msg("doRegister")}</a></span>
                </div>
            </div>
        </#if>
    </#if>

</@layout.registrationLayout>