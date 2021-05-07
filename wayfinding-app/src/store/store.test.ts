import { store } from './configureStore';

describe('configureStore', () => {
  it('should return a redux store', () => {
    expect(store).toEqual(
      expect.objectContaining({
        dispatch: expect.any(Function),
        subscribe: expect.any(Function),
        getState: expect.any(Function),
        replaceReducer: expect.any(Function),
      }),
    );
  });
});
