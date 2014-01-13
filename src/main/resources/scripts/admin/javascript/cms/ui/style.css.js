/**
 * processes text resource like css or javascript
 *
 * @param {string} text hooked text
 * @returns {string} text to send to browser
 */
function handleTextResource(text)
{
    text = text.replace(/font-size:.*12px;/g, 'font-size: 13px;');
    text = text.replace(/font-size:.*11px;/g, 'font-size: 12px;');

    return text;
}
