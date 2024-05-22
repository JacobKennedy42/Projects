import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'lastNameConverter'
})
export class LastNameConverterPipe implements PipeTransform {

  transform(name: string): string {
    return name[0];
  }

}
