//
//  ImageView.m
//  RCTImagePreview
//
//  Created by 欧阳伟坚 on 2018/3/21.
//  Copyright © 2018年 欧阳伟坚. All rights reserved.
//

#import "ImageView.h"
#import "MWPhotoBrowser.h"

@interface ImageView()<MWPhotoBrowserDelegate>

@property (nonatomic,strong) MWPhotoBrowser *browser;

@property (nonatomic,strong) NSMutableArray *data;

@property (nonatomic,strong) NSMutableArray *photos;

@property (nonatomic,assign) CGFloat num;

@end

@implementation ImageView

-(void)getImageData:(NSArray *)data index:(CGFloat)num{
    
    NSMutableArray *photo = [[NSMutableArray alloc] init];
    
    [[NSUserDefaults standardUserDefaults] setObject:data forKey:@"imageData"];
    
    MWPhoto *main;
    
    NSUserDefaults *user = [NSUserDefaults standardUserDefaults];
    
    if (save) {
        [user setObject:@"YES" forKey:@"isShowSave"];
    }
    else{
        [user setObject:@"NO" forKey:@"isShowSave"];
    }
    
    for (int i = 0; i < data.count; i++) {
        
        if ([data[i] hasPrefix:@"https://"] || [data[i] hasPrefix:@"http://"]) {

            main = [MWPhoto photoWithURL:[NSURL URLWithString:data[i]]];
            
        } else {
            UIImage *savedImage = [[UIImage alloc] initWithContentsOfFile:data[i]];
            
            main = [MWPhoto photoWithImage:savedImage];
        }
        
        [photo addObject:main];
    }
    
    self.photos = photo;
    
    _browser = [[MWPhotoBrowser alloc]initWithDelegate:self];
    
    // 设置默认值
    BOOL displayActionButton = NO;
    BOOL displaySelectionButtons = NO;
    BOOL displayNavArrows = NO;
    BOOL enableGrid = YES;
    BOOL startOnGrid = NO;
    BOOL autoPlayOnAppear = NO;
    
    // 设置browser属性
    _browser.displayActionButton = displayActionButton; //显示分享按钮(左右划动按钮显示才有效)
    _browser.displayNavArrows = displayNavArrows; //显示左右划动
    _browser.displaySelectionButtons = displaySelectionButtons; //是否显示选择图片按钮
    _browser.alwaysShowControls = displaySelectionButtons; //控制条始终显示
    _browser.zoomPhotosToFill = YES; //是否自适应大小
    _browser.enableGrid = enableGrid; //是否允许网络查看图片
    _browser.startOnGrid = startOnGrid; //是否以网格开始;
    _browser.enableSwipeToDismiss = NO;
    _browser.autoPlayOnAppear = autoPlayOnAppear; //是否自动播放视频
    [_browser setCurrentPhotoIndex:num];
    
    
    UINavigationController *nc = [[UINavigationController alloc] initWithRootViewController:_browser];

    nc.modalTransitionStyle = UIModalTransitionStyleCrossDissolve;

    [[self getPresentedViewController] presentViewController:nc animated:YES completion:nil];
}



- (UIViewController *)getPresentedViewController
{
    UIViewController *appRootVC = [UIApplication sharedApplication].keyWindow.rootViewController;
    UIViewController *topVC = appRootVC;
    if (topVC.presentedViewController) {
        topVC = topVC.presentedViewController;
    }
    
    return topVC;
}

#pragma mark - MWPhotoBrowserDelegate

- (NSUInteger)numberOfPhotosInPhotoBrowser:(MWPhotoBrowser *)photoBrowser {
    return _photos.count;
}

- (id <MWPhoto>)photoBrowser:(MWPhotoBrowser *)photoBrowser photoAtIndex:(NSUInteger)index {
    if (index < _photos.count)
        return [_photos objectAtIndex:index];
    return nil;
}


- (void)photoBrowser:(MWPhotoBrowser *)photoBrowser didDisplayPhotoAtIndex:(NSUInteger)index {
    
    NSLog(@"Did start viewing photo at index %lu", (unsigned long)index);
}

- (void)photoBrowserDidFinishModalPresentation:(MWPhotoBrowser *)photoBrowser {
    // If we subscribe to this method we must dismiss the view controller ourselves
    NSLog(@"Did finish modal presentation");
    
    [[self getPresentedViewController] dismissViewControllerAnimated:YES completion:nil];
}


@end
