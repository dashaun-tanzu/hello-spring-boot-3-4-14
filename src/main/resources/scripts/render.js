/*
 * The render function used by ScriptTemplateView.
 *
 * Spring reads the resolved template file and hands its contents to this
 * function as `template`. A real app would interpolate `model` values into the
 * markup; for this demo we just return the template text, which is enough to
 * render ordinary pages -- and, when the resolver is tricked into pointing at
 * an arbitrary file, enough to disclose that file's contents verbatim.
 */
function render(template, model, renderingContext) {
    return template;
}
