import { NativeModules, Platform } from 'react-native'

const { PicturePreview } = NativeModules

export function openPreview(imageData, index = 0, save = true) {

		PicturePreview.openPreview(imageData, index, save)
}