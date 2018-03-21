import { NativeModules, Platform } from 'react-native'

const { PicturePreview } = NativeModules

export function openPreview(imageData, index) {
    if(index) {
        PicturePreview.openPreview(imageData, index)
    } else { 
        PicturePreview.openPreview(imageData, 0) 
    }
}