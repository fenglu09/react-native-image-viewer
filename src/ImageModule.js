import { NativeModules, Platform } from 'react-native'

const { PicturePreview } = NativeModules

export function openPreview(imageData, index, save) {
    if(isEmpty(index) && isEmpty(save)){
        PicturePreview.openPreview(imageData, 0, true)
    } else if(!isEmpty(index) && isEmpty(save)) {
        PicturePreview.openPreview(imageData, index, true)
    } else if(isEmpty(index) && !isEmpty(save)){
        PicturePreview.openPreview(imageData, 0, save)
    } else if(!isEmpty(index) && !isEmpty(save)) {
        PicturePreview.openPreview(imageData, index, save)
    }
}

function isEmpty(v) {
    switch (typeof v) {
    case 'undefined':
        return true;
    case 'string':
        if (v.replace(/(^[ \t\n\r]*)|([ \t\n\r]*$)/g, '').length == 0) return true;
        break;
    case 'boolean':
        if (v != false) return true;
        break;
    case 'number':
        if (0 === v || isNaN(v)) return true;
        break;
    case 'object':
        if (null === v || v.length === 0) return true;
        for (var i in v) {
            return false;
        }
        return true;
    }
    return false;
}