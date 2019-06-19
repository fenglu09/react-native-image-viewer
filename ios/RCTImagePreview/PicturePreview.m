//
//  PicturePreview.m
//  RCTImagePreview
//
//  Created by 欧阳伟坚 on 2018/3/21.
//  Copyright © 2018年 欧阳伟坚. All rights reserved.
//

#import "PicturePreview.h"
#import "ImageView.h"

@implementation PicturePreview

// 暴露模块
RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(openPreview:(NSArray *)message index:(CGFloat)interval isShowSave:(BOOL)save) {
    dispatch_async(dispatch_get_main_queue(), ^{
        ImageView *test = [[ImageView alloc]initWithFrame:CGRectMake(0, 0, 0, 0)];
        [test getImageData:message index:interval isShowSave:save];
        [[UIApplication sharedApplication].keyWindow addSubview:test];
    });
}

@end
