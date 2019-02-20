# RangeSeekBar
Android Range Seekbar library 
[![](https://jitpack.io/v/Mohammed-Alaa/RangeSeekBar.svg)](https://jitpack.io/#Mohammed-Alaa/RangeSeekBar)
[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)  

## Usage

**For a working implementation of this project see the `app/` folder.**

### Step 1

Add this in your root build.gradle at the end of repositories
```groovy
allprojects {
		repositories {
			maven { url 'https://jitpack.io' }
		}
     
}
```

### Step 2

Include the library as a local library project or add the dependency in your build.gradle.

```groovy
dependencies {
    implementation 'com.github.Mohammed-Alaa:RangeSeekBar:1.0.1'
}
```	

### Step 3

Add the following xml to your layout file.

```xml
<com.mohammedalaa.seekbar.RangeSeekBarView
        android:id="@+id/range_seekbar"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"     
        app:barHeight="@dimen/value_bar_barHeight"
        app:baseColor="@color/navy"
        app:circleRadius="@dimen/value_bar_circleRadius"
        app:circleTextColor="@color/white"
        app:circleTextSize="@dimen/value_bar_circleTextSize"
        app:fillColor="@color/red"
        app:currentValue="60"
        app:minValue="15"
        app:maxValue="150"
        app:stepValue="5"    
        />
```

### Step 4

Reference the View in Java code.

```java
       RangeSeekBarView rangeSeekbar = (RangeSeekBarView) findViewById(R.id.range_seekbar);
```
Get Value
```java
       rangeSeekbar.getValue();
```
  ![](range_seekbar.gif)
  
  ## License

    Copyright 2018 Mohammmed Alaa
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
  
