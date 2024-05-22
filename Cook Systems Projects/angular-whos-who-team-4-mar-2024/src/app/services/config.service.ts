import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ConfigService {

  private explicit = new BehaviorSubject<boolean> (false)
  currentExplicit = this.explicit.asObservable();

  constructor() { }

  setExplicit = (value : boolean) => {
    this.explicit.next(value)
    console.log(this.explicit.getValue())
  }
}
