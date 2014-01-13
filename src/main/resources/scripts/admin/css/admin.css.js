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

    // make search box wider
    text += '\r\n\r\n[name="searchtext"]\r\n{\r\n    width: 250px;\r\n}';

    return text;
}
